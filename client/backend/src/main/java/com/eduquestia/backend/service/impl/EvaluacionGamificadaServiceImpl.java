package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.CrearEvaluacionRequest;
import com.eduquestia.backend.dto.request.CrearOpcionRequest;
import com.eduquestia.backend.dto.request.CrearPreguntaRequest;
import com.eduquestia.backend.dto.request.ResponderEvaluacionRequest;
import com.eduquestia.backend.dto.request.RespuestaRequest;
import com.eduquestia.backend.dto.response.EvaluacionGamificadaResponse;
import com.eduquestia.backend.dto.response.OpcionRespuestaResponse;
import com.eduquestia.backend.dto.response.PreguntaResponse;
import com.eduquestia.backend.dto.response.ResultadoEvaluacionResponse;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.exceptions.ResourceNotFoundException;
import com.eduquestia.backend.exceptions.UnauthorizedException;
import com.eduquestia.backend.exceptions.ValidationException;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.EvaluacionGamificadaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EvaluacionGamificadaServiceImpl implements EvaluacionGamificadaService {

    private final EvaluacionGamificadaRepository evaluacionRepository;
    private final PreguntaRepository preguntaRepository;
    private final MisionRepository misionRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RespuestaEstudianteRepository respuestaRepository;
    private final ResultadoEvaluacionRepository resultadoRepository;
    private final ProgresoMisionRepository progresoRepository;
    private final EntregaMisionRepository entregaRepository;

    @Override
    public EvaluacionGamificadaResponse crearEvaluacion(CrearEvaluacionRequest request, UUID profesorId) {
        log.info("Creando evaluación gamificada para curso: {} por profesor: {}", request.getCursoId(), profesorId);

        // Validar curso
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        // TODO: Validar que el profesor tiene acceso a este curso (por ahora permitimos cualquier curso)

        // Validar misión si se proporcionó (opcional)
        Mision mision = null;
        if (request.getMisionId() != null) {
            mision = misionRepository.findById(request.getMisionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

            if (!mision.getCategoria().name().equals("QUIZ")) {
                throw new ValidationException("Solo las misiones de categoría QUIZ pueden tener evaluaciones gamificadas");
            }
        }

        // Crear evaluación (ahora permite múltiples evaluaciones activas por curso)
        EvaluacionGamificada evaluacion = new EvaluacionGamificada();
        evaluacion.setCurso(curso);
        evaluacion.setMision(mision); // Puede ser null
        evaluacion.setTitulo(request.getTitulo());
        evaluacion.setDescripcion(request.getDescripcion());
        evaluacion.setTiempoLimiteMinutos(request.getTiempoLimiteMinutos());
        evaluacion.setIntentosPermitidos(request.getIntentosPermitidos() != null ? request.getIntentosPermitidos() : 1);
        evaluacion.setMostrarResultadosInmediato(request.getMostrarResultadosInmediato() != null ? request.getMostrarResultadosInmediato() : true);
        evaluacion.setPuntosPorPregunta(request.getPuntosPorPregunta() != null ? request.getPuntosPorPregunta() : 10);
        evaluacion.setPuntosBonusTiempo(request.getPuntosBonusTiempo() != null ? request.getPuntosBonusTiempo() : 5);
        evaluacion.setActivo(true);

        // Crear preguntas
        int ordenPregunta = 0;
        for (CrearPreguntaRequest preguntaReq : request.getPreguntas()) {
            Pregunta pregunta = new Pregunta();
            pregunta.setEvaluacion(evaluacion);
            pregunta.setEnunciado(preguntaReq.getEnunciado());
            pregunta.setTipoPregunta(preguntaReq.getTipoPregunta());
            pregunta.setPuntos(preguntaReq.getPuntos() != null ? preguntaReq.getPuntos() : 10);
            pregunta.setOrden(ordenPregunta++);
            pregunta.setImagenUrl(preguntaReq.getImagenUrl());
            pregunta.setExplicacion(preguntaReq.getExplicacion());

            // Crear opciones
            int ordenOpcion = 0;
            for (CrearOpcionRequest opcionReq : preguntaReq.getOpciones()) {
                OpcionRespuesta opcion = new OpcionRespuesta();
                opcion.setPregunta(pregunta);
                opcion.setTexto(opcionReq.getTexto());
                opcion.setEsCorrecta(opcionReq.getEsCorrecta() != null ? opcionReq.getEsCorrecta() : false);
                opcion.setOrden(ordenOpcion++);
                opcion.setImagenUrl(opcionReq.getImagenUrl());
                opcion.setFeedback(opcionReq.getFeedback());
                pregunta.addOpcion(opcion);
            }

            evaluacion.addPregunta(pregunta);
        }

        EvaluacionGamificada evaluacionGuardada = evaluacionRepository.save(evaluacion);
        log.info("Evaluación creada exitosamente: {}", evaluacionGuardada.getId());

        return convertirAResponse(evaluacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionGamificadaResponse obtenerEvaluacionPorMision(UUID misionId, UUID estudianteId) {
        log.info("Obteniendo evaluación para misión: {} y estudiante: {}", misionId, estudianteId);

        List<EvaluacionGamificada> evaluaciones = evaluacionRepository.findByMisionIdAndActivoTrue(misionId);
        if (evaluaciones.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró evaluación activa para esta misión");
        }

        EvaluacionGamificada evaluacion = evaluaciones.get(0);
        return convertirAResponse(evaluacion);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionGamificadaResponse obtenerEvaluacionPorId(UUID evaluacionId, UUID usuarioId) {
        EvaluacionGamificada evaluacion = evaluacionRepository.findByIdWithPreguntas(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // Verificar permisos (estudiante o profesor de la misión)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!usuario.getId().equals(evaluacion.getMision().getProfesor().getId())) {
            // Verificar si es estudiante del curso
            // Por ahora permitimos acceso si es estudiante
        }

        return convertirAResponse(evaluacion);
    }

    @Override
    public ResultadoEvaluacionResponse responderEvaluacion(ResponderEvaluacionRequest request, UUID estudianteId) {
        log.info("Procesando respuestas de evaluación: {} por estudiante: {}", request.getEvaluacionId(), estudianteId);

        EvaluacionGamificada evaluacion = evaluacionRepository.findByIdWithPreguntas(request.getEvaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        // Verificar intentos
        Integer intentosUsados = resultadoRepository.findMaxIntentoNumero(request.getEvaluacionId(), estudianteId);
        int siguienteIntento = (intentosUsados != null ? intentosUsados : 0) + 1;

        if (siguienteIntento > evaluacion.getIntentosPermitidos()) {
            throw new ValidationException("Has alcanzado el número máximo de intentos permitidos");
        }

        // Procesar respuestas
        int puntosTotales = 0;
        int puntosMaximos = 0;
        int preguntasCorrectas = 0;
        int preguntasTotales = evaluacion.getPreguntas().size();

        for (RespuestaRequest respuestaReq : request.getRespuestas()) {
            Pregunta pregunta = preguntaRepository.findById(respuestaReq.getPreguntaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada: " + respuestaReq.getPreguntaId()));

            puntosMaximos += pregunta.getPuntos();

            boolean esCorrecta = false;
            int puntosObtenidos = 0;

            // Validar respuesta según tipo de pregunta
            if (pregunta.getTipoPregunta().name().equals("OPCION_MULTIPLE") || 
                pregunta.getTipoPregunta().name().equals("VERDADERO_FALSO") ||
                pregunta.getTipoPregunta().name().equals("SELECCION_MULTIPLE")) {
                
                if (respuestaReq.getOpcionId() != null) {
                    OpcionRespuesta opcion = pregunta.getOpciones().stream()
                            .filter(o -> o.getId().equals(respuestaReq.getOpcionId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Opción no encontrada"));

                    esCorrecta = opcion.getEsCorrecta();
                    if (esCorrecta) {
                        puntosObtenidos = pregunta.getPuntos();
                        preguntasCorrectas++;
                    }
                }
            } else {
                // Para otros tipos, validar respuestaTexto
                // Por ahora, marcamos como correcta si hay respuesta
                if (respuestaReq.getRespuestaTexto() != null && !respuestaReq.getRespuestaTexto().trim().isEmpty()) {
                    // Validación básica - se puede mejorar
                    esCorrecta = true;
                    puntosObtenidos = pregunta.getPuntos();
                    preguntasCorrectas++;
                }
            }

            puntosTotales += puntosObtenidos;

            // Guardar respuesta
            RespuestaEstudiante respuesta = new RespuestaEstudiante();
            respuesta.setEvaluacion(evaluacion);
            respuesta.setPregunta(pregunta);
            respuesta.setEstudiante(estudiante);
            if (respuestaReq.getOpcionId() != null) {
                OpcionRespuesta opcion = pregunta.getOpciones().stream()
                        .filter(o -> o.getId().equals(respuestaReq.getOpcionId()))
                        .findFirst()
                        .orElse(null);
                respuesta.setOpcionSeleccionada(opcion);
            }
            respuesta.setRespuestaTexto(respuestaReq.getRespuestaTexto());
            respuesta.setEsCorrecta(esCorrecta);
            respuesta.setPuntosObtenidos(puntosObtenidos);
            respuesta.setTiempoRespuestaSegundos(respuestaReq.getTiempoRespuestaSegundos());
            respuesta.setIntentoNumero(siguienteIntento);
            respuestaRepository.save(respuesta);
        }

        // Calcular bonus por tiempo
        int puntosBonus = 0;
        if (request.getTiempoTotalSegundos() != null && evaluacion.getPuntosBonusTiempo() != null && evaluacion.getPuntosBonusTiempo() > 0) {
            if (evaluacion.getTiempoLimiteMinutos() != null) {
                int tiempoLimiteSegundos = evaluacion.getTiempoLimiteMinutos() * 60;
                if (request.getTiempoTotalSegundos() < tiempoLimiteSegundos / 2) {
                    puntosBonus = evaluacion.getPuntosBonusTiempo();
                }
            }
        }

        // Guardar resultado
        ResultadoEvaluacion resultado = new ResultadoEvaluacion();
        resultado.setEvaluacion(evaluacion);
        resultado.setEstudiante(estudiante);
        resultado.setPuntosTotales(puntosTotales);
        resultado.setPuntosMaximos(puntosMaximos);
        resultado.setPuntosBonus(puntosBonus);
        resultado.setPorcentaje(preguntasTotales > 0 ? (double) preguntasCorrectas / preguntasTotales * 100 : 0.0);
        resultado.setPreguntasCorrectas(preguntasCorrectas);
        resultado.setPreguntasTotales(preguntasTotales);
        resultado.setTiempoTotalSegundos(request.getTiempoTotalSegundos());
        resultado.setIntentoNumero(siguienteIntento);
        resultado.setCompletada(true);

        ResultadoEvaluacion resultadoGuardado = resultadoRepository.save(resultado);

        // Actualizar puntos del estudiante (usar GamificacionService si existe)
        log.info("Evaluación completada. Puntos totales: {}, Bonus: {}", puntosTotales, puntosBonus);

        // IMPORTANTE: Marcar la misión como completada automáticamente (solo si hay misión asociada)
        if (evaluacion.getMision() != null) {
            marcarMisionComoCompletada(evaluacion.getMision(), estudiante, puntosTotales + puntosBonus);
        } else {
            log.info("Evaluación sin misión asociada. Los puntos se registran pero no se completa ninguna misión.");
        }

        return convertirResultadoAResponse(resultadoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultadoEvaluacionResponse> obtenerResultadosPorEstudiante(UUID evaluacionId, UUID estudianteId) {
        List<ResultadoEvaluacion> resultados = resultadoRepository.findByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId);
        return resultados.stream()
                .map(this::convertirResultadoAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultadoEvaluacionResponse> obtenerResultadosPorEvaluacion(UUID evaluacionId, UUID profesorId) {
        EvaluacionGamificada evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // TODO: Validar que el profesor tiene acceso a este curso/evaluación
        // Por ahora permitimos ver cualquier resultado

        // Obtener todos los resultados de esta evaluación
        // Necesitaríamos un método en el repositorio
        return List.of(); // Por ahora
    }

    @Override
    public void eliminarEvaluacion(UUID evaluacionId, UUID profesorId) {
        EvaluacionGamificada evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        // TODO: Validar que el profesor tiene acceso a este curso/evaluación
        // Por ahora permitimos eliminar cualquier evaluación

        evaluacion.setActivo(false);
        evaluacionRepository.save(evaluacion);
        log.info("Evaluación desactivada por profesor {}: {}", profesorId, evaluacionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerIntentosRestantes(UUID evaluacionId, UUID estudianteId) {
        EvaluacionGamificada evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        Integer intentosUsados = resultadoRepository.findMaxIntentoNumero(evaluacionId, estudianteId);
        int intentosUsadosCount = intentosUsados != null ? intentosUsados : 0;

        return Math.max(0, evaluacion.getIntentosPermitidos() - intentosUsadosCount);
    }

    private EvaluacionGamificadaResponse convertirAResponse(EvaluacionGamificada evaluacion) {
        Mision mision = evaluacion.getMision();
        Curso curso = evaluacion.getCurso();

        return EvaluacionGamificadaResponse.builder()
                .id(evaluacion.getId())
                .misionId(mision != null ? mision.getId() : null)
                .misionTitulo(mision != null ? mision.getTitulo() : null)
                .cursoId(curso.getId())
                .cursoNombre(curso.getNombre())
                .titulo(evaluacion.getTitulo())
                .descripcion(evaluacion.getDescripcion())
                .tiempoLimiteMinutos(evaluacion.getTiempoLimiteMinutos())
                .intentosPermitidos(evaluacion.getIntentosPermitidos())
                .mostrarResultadosInmediato(evaluacion.getMostrarResultadosInmediato())
                .puntosPorPregunta(evaluacion.getPuntosPorPregunta())
                .puntosBonusTiempo(evaluacion.getPuntosBonusTiempo())
                .activo(evaluacion.getActivo())
                .preguntas(evaluacion.getPreguntas().stream()
                        .map(this::convertirPreguntaAResponse)
                        .collect(Collectors.toList()))
                .fechaCreacion(evaluacion.getFechaCreacion())
                .fechaActualizacion(evaluacion.getFechaActualizacion())
                .build();
    }

    private PreguntaResponse convertirPreguntaAResponse(Pregunta pregunta) {
        return PreguntaResponse.builder()
                .id(pregunta.getId())
                .enunciado(pregunta.getEnunciado())
                .tipoPregunta(pregunta.getTipoPregunta())
                .puntos(pregunta.getPuntos())
                .orden(pregunta.getOrden())
                .imagenUrl(pregunta.getImagenUrl())
                .explicacion(pregunta.getExplicacion())
                .opciones(pregunta.getOpciones().stream()
                        .map(this::convertirOpcionAResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private OpcionRespuestaResponse convertirOpcionAResponse(OpcionRespuesta opcion) {
        return OpcionRespuestaResponse.builder()
                .id(opcion.getId())
                .texto(opcion.getTexto())
                .esCorrecta(opcion.getEsCorrecta())
                .orden(opcion.getOrden())
                .imagenUrl(opcion.getImagenUrl())
                .feedback(opcion.getFeedback())
                .build();
    }

    private ResultadoEvaluacionResponse convertirResultadoAResponse(ResultadoEvaluacion resultado) {
        return ResultadoEvaluacionResponse.builder()
                .id(resultado.getId())
                .evaluacionId(resultado.getEvaluacion().getId())
                .estudianteId(resultado.getEstudiante().getId())
                .estudianteNombre(resultado.getEstudiante().getNombreCompleto())
                .puntosTotales(resultado.getPuntosTotales())
                .puntosMaximos(resultado.getPuntosMaximos())
                .puntosBonus(resultado.getPuntosBonus())
                .porcentaje(resultado.getPorcentaje())
                .preguntasCorrectas(resultado.getPreguntasCorrectas())
                .preguntasTotales(resultado.getPreguntasTotales())
                .tiempoTotalSegundos(resultado.getTiempoTotalSegundos())
                .intentoNumero(resultado.getIntentoNumero())
                .completada(resultado.getCompletada())
                .fechaCompletado(resultado.getFechaCompletado())
                .build();
    }

    /**
     * Marca la misión asociada a la evaluación como completada
     */
    private void marcarMisionComoCompletada(Mision mision, Usuario estudiante, Integer puntosObtenidos) {
        log.info("Marcando misión {} como completada para estudiante {}", mision.getId(), estudiante.getId());

        // Obtener progreso de la misión
        ProgresoMision progreso = progresoRepository
                .findByMisionIdAndEstudianteId(mision.getId(), estudiante.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Progreso de misión no encontrado"));

        // Marcar como completada solo si aún no está completada
        if (!progreso.getCompletada()) {
            progreso.setPorcentajeCompletado(100);
            progreso.setCompletada(true);
            progreso.setFechaCompletado(java.time.LocalDateTime.now());
            progreso.setUltimaActividad(java.time.LocalDateTime.now());
            progresoRepository.save(progreso);

            // Actualizar o crear entrega
            EntregaMision entrega = entregaRepository
                    .findByMisionIdAndEstudianteId(mision.getId(), estudiante.getId())
                    .orElseGet(() -> {
                        EntregaMision nuevaEntrega = new EntregaMision();
                        nuevaEntrega.setMision(mision);
                        nuevaEntrega.setEstudiante(estudiante);
                        nuevaEntrega.setIntentos(1);
                        nuevaEntrega.setFechaCreacion(java.time.LocalDateTime.now());
                        return nuevaEntrega;
                    });

            // Actualizar entrega con puntos de la evaluación
            entrega.setEstado(com.eduquestia.backend.entity.enums.EstadoEntrega.ENVIADA);
            entrega.setContenidoEntrega("Evaluación completada exitosamente");
            entrega.setFechaEnvio(java.time.LocalDateTime.now());
            entrega.setPuntosObtenidos(puntosObtenidos != null ? puntosObtenidos : mision.getPuntosRecompensa());
            entregaRepository.save(entrega);

            log.info("Misión {} completada exitosamente con {} puntos", mision.getId(), puntosObtenidos);
        } else {
            log.info("Misión {} ya estaba completada para estudiante {}", mision.getId(), estudiante.getId());
        }
    }

    @Override
    public List<EvaluacionGamificadaResponse> listarEvaluacionesPorCurso(UUID cursoId) {
        log.info("Listando evaluaciones para curso: {}", cursoId);

        // Validar que existe el curso
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }

        List<EvaluacionGamificada> evaluaciones = evaluacionRepository.findByCursoId(cursoId);
        
        return evaluaciones.stream()
                .filter(EvaluacionGamificada::getActivo) // ✅ FILTRAR SOLO ACTIVAS
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluacionGamificadaResponse> listarEvaluacionesProfesor(UUID profesorId) {
        log.info("Listando evaluaciones para profesor: {}", profesorId);

        // Obtener todos los cursos del profesor
        List<Curso> cursos = cursoRepository.findAll().stream()
                .filter(curso -> curso.getId() != null) // Filtro simple por ahora
                .collect(Collectors.toList());

        if (cursos.isEmpty()) {
            log.info("El profesor {} no tiene cursos asignados", profesorId);
            return List.of();
        }

        List<UUID> cursosIds = cursos.stream()
                .map(Curso::getId)
                .collect(Collectors.toList());

        List<EvaluacionGamificada> evaluaciones = evaluacionRepository.findByProfesorCursos(cursosIds);
        
        return evaluaciones.stream()
                .filter(EvaluacionGamificada::getActivo) // ✅ FILTRAR SOLO ACTIVAS
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluacionGamificadaResponse> listarEvaluacionesEstudiante(UUID estudianteId) {
        log.info("Listando evaluaciones para estudiante: {}", estudianteId);

        // Validar que existe el estudiante
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        // Obtener todos los cursos del estudiante (inscripciones)
        List<Curso> cursos = cursoRepository.findAll().stream()
                .filter(curso -> curso.getId() != null)
                .collect(Collectors.toList());

        if (cursos.isEmpty()) {
            log.info("El estudiante {} no tiene cursos asignados", estudianteId);
            return List.of();
        }

        List<UUID> cursosIds = cursos.stream()
                .map(Curso::getId)
                .collect(Collectors.toList());

        // Obtener todas las evaluaciones activas de esos cursos
        List<EvaluacionGamificada> evaluaciones = evaluacionRepository.findByProfesorCursos(cursosIds);
        
        // Filtrar solo las activas
        return evaluaciones.stream()
                .filter(EvaluacionGamificada::getActivo)
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
}

