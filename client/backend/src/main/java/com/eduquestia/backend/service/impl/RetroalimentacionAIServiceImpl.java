package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.GenerarRetroalimentacionRequest;
import com.eduquestia.backend.dto.response.RetroalimentacionResponse;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.exception.ResourceNotFoundException;
import com.eduquestia.backend.exception.UnauthorizedException;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.GeminiService;
import com.eduquestia.backend.service.RetroalimentacionAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetroalimentacionAIServiceImpl implements RetroalimentacionAIService {

    private final GeminiService geminiService;
    private final UsuarioRepository usuarioRepository;
    private final EvaluacionGamificadaRepository evaluacionRepository;
    private final ResultadoEvaluacionRepository resultadoRepository;
    private final RespuestaEstudianteRepository respuestaRepository;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public RetroalimentacionResponse generarRetroalimentacion(GenerarRetroalimentacionRequest request, UUID profesorId) {
        log.info("Generando retroalimentación para estudiante {} en evaluación {}", 
                request.getEstudianteId(), request.getEvaluacionId());

        // Validar que el estudiante existe
        Usuario estudiante = usuarioRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        if (!"estudiante".equalsIgnoreCase(estudiante.getRol())) {
            throw new IllegalArgumentException("El usuario no es un estudiante");
        }

        // Validar que la evaluación existe
        EvaluacionGamificada evaluacion = evaluacionRepository.findByIdWithPreguntas(request.getEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // Validar que el profesor tiene acceso a esta evaluación
        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado"));

        // Verificar que el profesor es dueño del curso de la evaluación
        // Esto se puede hacer verificando si el profesor tiene acceso al curso
        // Por ahora, asumimos que si el profesor solicita la retroalimentación, tiene acceso

        // Obtener todos los resultados del estudiante en esta evaluación
        List<ResultadoEvaluacion> resultados = resultadoRepository
                .findByEvaluacionIdAndEstudianteId(request.getEvaluacionId(), request.getEstudianteId());

        if (resultados.isEmpty()) {
            throw new ResourceNotFoundException("El estudiante no ha completado esta evaluación");
        }

        // Obtener el mejor resultado (último intento o mejor puntaje)
        ResultadoEvaluacion mejorResultado = resultados.stream()
                .max((r1, r2) -> Double.compare(r1.getPorcentaje(), r2.getPorcentaje()))
                .orElse(resultados.get(resultados.size() - 1));

        // Obtener las respuestas del estudiante para el mejor resultado
        List<RespuestaEstudiante> respuestas = respuestaRepository
                .findByEvaluacionIdAndEstudianteIdAndIntentoNumero(
                        request.getEvaluacionId(), 
                        request.getEstudianteId(), 
                        mejorResultado.getIntentoNumero()
                );

        // Construir el contexto para la IA
        String contexto = construirContextoRetroalimentacion(estudiante, evaluacion, mejorResultado, respuestas);

        // Generar retroalimentación usando Gemini AI
        String systemPrompt = """
                Eres un asistente educativo experto en retroalimentación personalizada para estudiantes.
                
                Tu tarea es generar retroalimentación constructiva y motivadora basada en el desempeño del estudiante en una evaluación.
                
                La retroalimentación debe:
                - Ser personalizada y específica al desempeño del estudiante
                - Identificar fortalezas y áreas de mejora
                - Proporcionar sugerencias concretas para mejorar
                - Ser motivadora y alentadora
                - Estar escrita en español
                - Tener un tono profesional pero cercano
                - Incluir recomendaciones de estudio o práctica adicional
                
                Responde SOLO con la retroalimentación, sin encabezados ni formato adicional.
                """;

        String retroalimentacion = geminiService.generateResponse(systemPrompt, contexto);

        log.info("Retroalimentación generada exitosamente para estudiante {}", request.getEstudianteId());

        return RetroalimentacionResponse.builder()
                .estudianteId(estudiante.getId())
                .estudianteNombre(estudiante.getNombreCompleto())
                .evaluacionId(evaluacion.getId())
                .evaluacionTitulo(evaluacion.getTitulo())
                .retroalimentacion(retroalimentacion)
                .fechaGeneracion(LocalDateTime.now())
                .build();
    }

    private String construirContextoRetroalimentacion(
            Usuario estudiante,
            EvaluacionGamificada evaluacion,
            ResultadoEvaluacion resultado,
            List<RespuestaEstudiante> respuestas) {

        StringBuilder contexto = new StringBuilder();
        contexto.append("INFORMACIÓN DEL ESTUDIANTE:\n");
        contexto.append("Nombre: ").append(estudiante.getNombreCompleto()).append("\n\n");

        contexto.append("INFORMACIÓN DE LA EVALUACIÓN:\n");
        contexto.append("Título: ").append(evaluacion.getTitulo()).append("\n");
        if (evaluacion.getDescripcion() != null && !evaluacion.getDescripcion().isEmpty()) {
            contexto.append("Descripción: ").append(evaluacion.getDescripcion()).append("\n");
        }
        contexto.append("\n");

        contexto.append("RESULTADOS DEL ESTUDIANTE:\n");
        contexto.append("Puntos obtenidos: ").append(resultado.getPuntosTotales()).append(" / ")
                .append(resultado.getPuntosMaximos()).append("\n");
        contexto.append("Porcentaje: ").append(String.format("%.2f", resultado.getPorcentaje())).append("%\n");
        contexto.append("Preguntas correctas: ").append(resultado.getPreguntasCorrectas()).append(" / ")
                .append(resultado.getPreguntasTotales()).append("\n");
        contexto.append("Intento número: ").append(resultado.getIntentoNumero()).append("\n");
        if (resultado.getTiempoTotalSegundos() != null) {
            int minutos = resultado.getTiempoTotalSegundos() / 60;
            int segundos = resultado.getTiempoTotalSegundos() % 60;
            contexto.append("Tiempo empleado: ").append(minutos).append(" minutos y ")
                    .append(segundos).append(" segundos\n");
        }
        contexto.append("\n");

        // Analizar respuestas incorrectas
        List<RespuestaEstudiante> respuestasIncorrectas = respuestas.stream()
                .filter(r -> !r.getEsCorrecta())
                .collect(Collectors.toList());

        if (!respuestasIncorrectas.isEmpty()) {
            contexto.append("ÁREAS DE MEJORA (Preguntas incorrectas):\n");
            for (RespuestaEstudiante respuesta : respuestasIncorrectas) {
                contexto.append("- ").append(respuesta.getPregunta().getEnunciado()).append("\n");
                if (respuesta.getPregunta().getExplicacion() != null && 
                    !respuesta.getPregunta().getExplicacion().isEmpty()) {
                    contexto.append("  Explicación: ").append(respuesta.getPregunta().getExplicacion()).append("\n");
                }
            }
            contexto.append("\n");
        }

        // Analizar respuestas correctas
        long respuestasCorrectas = respuestas.stream()
                .filter(RespuestaEstudiante::getEsCorrecta)
                .count();

        if (respuestasCorrectas > 0) {
            contexto.append("FORTALEZAS:\n");
            contexto.append("El estudiante respondió correctamente ").append(respuestasCorrectas)
                    .append(" de ").append(respuestas.size()).append(" preguntas.\n");
            contexto.append("\n");
        }

        contexto.append("Por favor, genera una retroalimentación personalizada y constructiva basada en esta información.");

        return contexto.toString();
    }
}

