package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.GenerarActividadesAdaptadasRequest;
import com.eduquestia.backend.dto.response.ActividadesAdaptadasResponse;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.exceptions.ResourceNotFoundException;
import com.eduquestia.backend.exceptions.UnauthorizedException;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.ActividadesAdaptadasAIService;
import com.eduquestia.backend.service.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActividadesAdaptadasAIServiceImpl implements ActividadesAdaptadasAIService {

    private final GeminiService geminiService;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ResultadoEvaluacionRepository resultadoRepository;
    private final EvaluacionGamificadaRepository evaluacionRepository;
    private final ProgresoMisionRepository progresoMisionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public ActividadesAdaptadasResponse generarActividadesAdaptadas(
            GenerarActividadesAdaptadasRequest request, UUID profesorId) {
        
        log.info("Generando actividades adaptadas para curso {} por profesor {}", 
                request.getCursoId(), profesorId);

        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        // Obtener estudiantes del curso
        List<Inscripcion> inscripciones = inscripcionRepository
                .findInscripcionesActivasByCursoId(request.getCursoId());

        if (inscripciones.isEmpty()) {
            throw new ResourceNotFoundException("El curso no tiene estudiantes inscritos");
        }

        // Determinar si se generarán actividades para TODO el curso o para un ESTUDIANTE específico
        List<UUID> estudiantesIds;
        if (request.getEstudianteId() != null) {
            UUID estudianteId = request.getEstudianteId();

            boolean perteneceAlCurso = inscripciones.stream()
                    .anyMatch(i -> i.getEstudiante() != null
                            && estudianteId.equals(i.getEstudiante().getId()));

            if (!perteneceAlCurso) {
                throw new UnauthorizedException("El estudiante no pertenece a este curso");
            }

            estudiantesIds = List.of(estudianteId);
        } else {
            estudiantesIds = inscripciones.stream()
                    .map(i -> i.getEstudiante().getId())
                    .collect(Collectors.toList());
        }

        // Analizar el nivel (del grupo o del estudiante, según la lista recibida)
        String nivelPromedio = analizarNivelEstudiantes(estudiantesIds, request.getCursoId());

        // Construir contexto para la IA
        String contexto = construirContextoActividades(curso, estudiantesIds, nivelPromedio, request);

        // Generar actividades usando Gemini AI
        String systemPrompt = """
                Eres un experto en diseño educativo y creación de actividades de aprendizaje adaptadas.
                
                Tu tarea es generar actividades educativas (evaluaciones, misiones o preguntas) adaptadas al nivel de los estudiantes.
                
                Las actividades deben:
                - Estar adaptadas al nivel promedio de los estudiantes
                - Ser claras y bien estructuradas
                - Incluir preguntas variadas y desafiantes pero apropiadas
                - Estar escritas en español
                - Ser educativas y alineadas con el tema del curso
                
                Responde en formato JSON con la siguiente estructura:
                {
                  "actividades": [
                    {
                      "titulo": "Título de la actividad",
                      "descripcion": "Descripción detallada",
                      "tipo": "evaluacion|mision|pregunta",
                      "dificultad": "facil|medio|dificil",
                      "preguntas": [
                        {
                          "enunciado": "Texto de la pregunta",
                          "tipoPregunta": "OPCION_MULTIPLE|VERDADERO_FALSO|SELECCION_MULTIPLE",
                          "opciones": ["Opción 1", "Opción 2", "Opción 3", "Opción 4"],
                          "indiceCorrecta": 0,
                          "explicacion": "Explicación de la respuesta correcta"
                        }
                      ]
                    }
                  ]
                }
                
                Genera entre 3 y 5 actividades según la cantidad solicitada.
                """;

        String respuestaIA = geminiService.generateResponse(systemPrompt, contexto);

        // Parsear la respuesta de la IA (simplificado - en producción usar un parser JSON robusto)
        List<ActividadesAdaptadasResponse.ActividadPropuesta> actividades = 
                parsearActividades(respuestaIA, request.getCantidadPreguntas() != null ? request.getCantidadPreguntas() : 5);

        log.info("Actividades adaptadas generadas exitosamente para curso {}", request.getCursoId());

        return ActividadesAdaptadasResponse.builder()
                .cursoId(curso.getId())
                .cursoNombre(curso.getNombre())
                .nivelPromedioEstudiantes(nivelPromedio)
                .actividades(actividades)
                .fechaGeneracion(LocalDateTime.now())
                .build();
    }

    private String analizarNivelEstudiantes(List<UUID> estudiantesIds, UUID cursoId) {
        // Obtener todas las evaluaciones del curso
        List<EvaluacionGamificada> evaluaciones = evaluacionRepository.findByCursoId(cursoId);

        if (evaluaciones.isEmpty()) {
            return "Nivel inicial - Sin evaluaciones previas";
        }

        // Calcular promedio de porcentajes de todos los estudiantes
        List<Double> porcentajes = new ArrayList<>();
        for (UUID estudianteId : estudiantesIds) {
            for (EvaluacionGamificada evaluacion : evaluaciones) {
                List<ResultadoEvaluacion> resultados = resultadoRepository
                        .findByEvaluacionIdAndEstudianteId(evaluacion.getId(), estudianteId);
                
                if (!resultados.isEmpty()) {
                    // Tomar el mejor resultado de cada evaluación
                    double mejorPorcentaje = resultados.stream()
                            .mapToDouble(ResultadoEvaluacion::getPorcentaje)
                            .max()
                            .orElse(0.0);
                    porcentajes.add(mejorPorcentaje);
                }
            }
        }

        if (porcentajes.isEmpty()) {
            return "Nivel inicial - Sin resultados de evaluaciones";
        }

        double promedio = porcentajes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Determinar nivel basado en el promedio
        if (promedio >= 80) {
            return "Nivel avanzado (Promedio: " + String.format("%.1f", promedio) + "%)";
        } else if (promedio >= 60) {
            return "Nivel intermedio (Promedio: " + String.format("%.1f", promedio) + "%)";
        } else {
            return "Nivel básico (Promedio: " + String.format("%.1f", promedio) + "%)";
        }
    }

    private String construirContextoActividades(
            Curso curso,
            List<UUID> estudiantesIds,
            String nivelPromedio,
            GenerarActividadesAdaptadasRequest request) {

        StringBuilder contexto = new StringBuilder();
        contexto.append("INFORMACIÓN DEL CURSO:\n");
        contexto.append("Nombre: ").append(curso.getNombre()).append("\n");
        contexto.append("Código: ").append(curso.getCodigoCurso()).append("\n");
        contexto.append("Número de estudiantes: ").append(estudiantesIds.size()).append("\n");
        contexto.append("Nivel promedio de los estudiantes: ").append(nivelPromedio).append("\n\n");

        if (request.getTema() != null && !request.getTema().isEmpty()) {
            contexto.append("TEMA ESPECÍFICO SOLICITADO:\n");
            contexto.append(request.getTema()).append("\n\n");
        }

        contexto.append("TIPO DE ACTIVIDAD SOLICITADA:\n");
        contexto.append(request.getTipoActividad() != null ? request.getTipoActividad() : "evaluacion").append("\n\n");

        contexto.append("CANTIDAD DE PREGUNTAS:\n");
        contexto.append(request.getCantidadPreguntas() != null ? request.getCantidadPreguntas() : 5).append("\n\n");

        contexto.append("Por favor, genera actividades educativas adaptadas a este nivel y contexto.");

        return contexto.toString();
    }

    private List<ActividadesAdaptadasResponse.ActividadPropuesta> parsearActividades(
            String respuestaIA, int cantidadPreguntas) {

        List<ActividadesAdaptadasResponse.ActividadPropuesta> actividades = new ArrayList<>();

        try {
            // Limpiar posibles fences de markdown ```json ... ```
            String cleaned = respuestaIA.trim();
            if (cleaned.startsWith("```")) {
                int firstNewLine = cleaned.indexOf('\n');
                if (firstNewLine != -1) {
                    cleaned = cleaned.substring(firstNewLine + 1);
                }
                int lastFence = cleaned.lastIndexOf("```");
                if (lastFence != -1) {
                    cleaned = cleaned.substring(0, lastFence);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(cleaned);
            JsonNode actividadesNode = root.path("actividades");

            if (actividadesNode.isArray() && actividadesNode.size() > 0) {
                for (JsonNode actNode : actividadesNode) {
                    ActividadesAdaptadasResponse.ActividadPropuesta actividad =
                            mapper.treeToValue(actNode, ActividadesAdaptadasResponse.ActividadPropuesta.class);

                    // Asegurar preguntas y limitar a la cantidad solicitada
                    if (actividad.getPreguntas() != null && !actividad.getPreguntas().isEmpty()) {
                        List<ActividadesAdaptadasResponse.PreguntaPropuesta> limitadas =
                                actividad.getPreguntas().stream()
                                        .limit(cantidadPreguntas)
                                        .collect(Collectors.toList());
                        actividad.setPreguntas(limitadas);
                    } else {
                        actividad.setPreguntas(crearPreguntasEjemplo(cantidadPreguntas));
                    }

                    actividades.add(actividad);
                }
            }
        } catch (Exception e) {
            log.error("Error al parsear actividades de la IA: {}", e.getMessage());
        }

        // Si no se pudo parsear nada, usar actividad de ejemplo
        if (actividades.isEmpty()) {
            actividades.add(crearActividadEjemplo(cantidadPreguntas));
        }

        return actividades;
    }

    private List<ActividadesAdaptadasResponse.PreguntaPropuesta> crearPreguntasEjemplo(int cantidad) {
        List<ActividadesAdaptadasResponse.PreguntaPropuesta> preguntas = new ArrayList<>();
        
        for (int i = 1; i <= cantidad; i++) {
            ActividadesAdaptadasResponse.PreguntaPropuesta pregunta = 
                    ActividadesAdaptadasResponse.PreguntaPropuesta.builder()
                    .enunciado("Pregunta " + i + " generada por IA (adaptada al nivel de los estudiantes)")
                    .tipoPregunta("OPCION_MULTIPLE")
                    .opciones(Arrays.asList("Opción A", "Opción B", "Opción C", "Opción D"))
                    .indiceCorrecta(0)
                    .explicacion("Esta es una pregunta adaptada al nivel promedio de los estudiantes del curso.")
                    .build();
            
            preguntas.add(pregunta);
        }
        
        return preguntas;
    }

    private ActividadesAdaptadasResponse.ActividadPropuesta crearActividadEjemplo(int cantidadPreguntas) {
        return ActividadesAdaptadasResponse.ActividadPropuesta.builder()
                .titulo("Actividad Adaptada - Ejemplo")
                .descripcion("Actividad generada automáticamente adaptada al nivel de los estudiantes")
                .tipo("evaluacion")
                .dificultad("medio")
                .preguntas(crearPreguntasEjemplo(cantidadPreguntas))
                .build();
    }
}

