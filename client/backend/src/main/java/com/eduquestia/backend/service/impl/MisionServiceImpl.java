package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.CompletarMisionRequest;
import com.eduquestia.backend.dto.request.ContenidoRequest;
import com.eduquestia.backend.dto.request.CriterioRequest;
import com.eduquestia.backend.dto.request.MisionCreateRequest;
import com.eduquestia.backend.dto.request.MisionUpdateRequest;
import com.eduquestia.backend.dto.response.*;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.EstadoEntrega;
import com.eduquestia.backend.exceptions.ResourceNotFoundException;
import com.eduquestia.backend.exceptions.UnauthorizedException;
import com.eduquestia.backend.exceptions.ValidationException;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.GamificacionService;
import com.eduquestia.backend.service.MisionService;
import com.eduquestia.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class MisionServiceImpl implements MisionService {

    private final MisionRepository misionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final ContenidoMisionRepository contenidoRepository;
    private final CriteriosEvaluacionRepository criteriosRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ProgresoMisionRepository progresoRepository;
    private final EntregaMisionRepository entregaRepository;
    private final NotificacionService notificacionService;
    private final GamificacionService gamificacionService;

    @Override
    public MisionResponse crearMision(MisionCreateRequest request, UUID profesorId) {
        log.info("Creando misión: {} para curso: {}", request.getTitulo(), request.getCursoId());

        // Validar profesor
        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + profesorId));

        // Validar curso
        UUID cursoId = request.getCursoId();
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + cursoId));

        // Validar fechas
        validarFechas(request.getFechaInicio(), request.getFechaLimite());

        // Crear misión
        Mision mision = new Mision();
        mision.setCurso(curso);
        mision.setProfesor(profesor);
        mision.setTitulo(request.getTitulo());
        mision.setDescripcion(request.getDescripcion());
        mision.setTipoMision(request.getTipoMision());
        mision.setCategoria(request.getCategoria());
        mision.setDificultad(request.getDificultad());
        mision.setPuntosRecompensa(request.getPuntosRecompensa());
        mision.setExperienciaRecompensa(request.getExperienciaRecompensa());
        mision.setFechaInicio(request.getFechaInicio());
        mision.setFechaLimite(request.getFechaLimite());
        mision.setRequisitosPrevios(request.getRequisitosPrevios());
        mision.setActivo(true);

        // Guardar misión
        mision = misionRepository.save(mision);

        // Crear contenidos
        if (request.getContenidos() != null && !request.getContenidos().isEmpty()) {
            crearContenidos(mision, request.getContenidos());
        }

        // Crear criterios de evaluación
        if (request.getCriterios() != null && !request.getCriterios().isEmpty()) {
            crearCriterios(mision, request.getCriterios());
        }

        // Crear progreso inicial para todos los estudiantes del curso
        crearProgresoInicialParaEstudiantes(mision);

        // Crear notificaciones para estudiantes
        notificarNuevaMision(mision);

        log.info("Misión creada exitosamente con ID: {}", mision.getId());

        return convertirAMisionResponse(mision);
    }

    @Override
    @Transactional(readOnly = true)
    public MisionResponse obtenerMisionPorId(UUID misionId) {
        log.info("Obteniendo misión con ID: {}", misionId);

        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        return convertirAMisionResponse(mision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MisionListResponse> listarMisionesPorProfesor(UUID profesorId) {
        log.info("Listando misiones del profesor: {}", profesorId);

        List<Mision> misiones = misionRepository.findMisionesByProfesor(profesorId);

        return misiones.stream()
                .map(this::convertirAMisionListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MisionListResponse> listarMisionesPorCurso(UUID cursoId) {
        log.info("Listando misiones del curso: {}", cursoId);

        List<Mision> misiones = misionRepository.findByCursoIdAndActivoTrue(cursoId);

        return misiones.stream()
                .map(this::convertirAMisionListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MisionListResponse> listarMisionesPorCursoYCategoria(
            UUID cursoId, CategoriaMision categoria) {
        log.info("Listando misiones del curso: {} con categoría: {}", cursoId, categoria);

        List<Mision> misiones = misionRepository.findByCursoIdAndCategoria(cursoId, categoria);

        return misiones.stream()
                .map(this::convertirAMisionListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MisionResponse actualizarMision(
            UUID misionId, MisionUpdateRequest request, UUID profesorId) {
        log.info("Actualizando misión: {}", misionId);

        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        // Verificar que el profesor sea el dueño de la misión
        if (!mision.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para actualizar esta misión");
        }

        // Verificar si hay entregas enviadas
        Long entregasCount = entregaRepository.countByMisionId(misionId);
        if (entregasCount > 0) {
            log.warn("La misión {} tiene entregas, actualización limitada", misionId);
            // Solo permitir actualizar fechas y estado si ya hay entregas
            if (request.getFechaLimite() != null) {
                validarFechaLimite(request.getFechaLimite());
                mision.setFechaLimite(request.getFechaLimite());
            }
            if (request.getActivo() != null) {
                mision.setActivo(request.getActivo());
            }
        } else {
            // Actualizar todos los campos permitidos
            if (request.getTitulo() != null) {
                mision.setTitulo(request.getTitulo());
            }
            if (request.getDescripcion() != null) {
                mision.setDescripcion(request.getDescripcion());
            }
            if (request.getTipoMision() != null) {
                mision.setTipoMision(request.getTipoMision());
            }
            if (request.getCategoria() != null) {
                mision.setCategoria(request.getCategoria());
            }
            if (request.getDificultad() != null) {
                mision.setDificultad(request.getDificultad());
            }
            if (request.getPuntosRecompensa() != null) {
                mision.setPuntosRecompensa(request.getPuntosRecompensa());
            }
            if (request.getExperienciaRecompensa() != null) {
                mision.setExperienciaRecompensa(request.getExperienciaRecompensa());
            }
            if (request.getFechaInicio() != null) {
                mision.setFechaInicio(request.getFechaInicio());
            }
            if (request.getFechaLimite() != null) {
                validarFechaLimite(request.getFechaLimite());
                mision.setFechaLimite(request.getFechaLimite());
            }
            if (request.getActivo() != null) {
                mision.setActivo(request.getActivo());
            }
            if (request.getRequisitosPrevios() != null) {
                mision.setRequisitosPrevios(request.getRequisitosPrevios());
            }
        }

        mision = misionRepository.save(mision);

        log.info("Misión actualizada exitosamente: {}", misionId);

        return convertirAMisionResponse(mision);
    }

    @Override
    public void eliminarMision(UUID misionId, UUID profesorId) {
        log.info("Eliminando misión: {}", misionId);

        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        // Verificar que el profesor sea el dueño
        if (!mision.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para eliminar esta misión");
        }

        // Verificar si hay entregas
        Long entregasCount = entregaRepository.countByMisionId(misionId);
        if (entregasCount > 0) {
            throw new ValidationException(
                    "No se puede eliminar la misión porque ya tiene entregas. " +
                            "Puedes desactivarla en su lugar."
            );
        }

        // Eliminar progreso, contenidos y criterios (cascade)
        misionRepository.delete(mision);

        log.info("Misión eliminada exitosamente: {}", misionId);
    }

    // CONTINÚA EN LA PARTE 2...
    // ... CONTINUACIÓN DE MisionServiceImpl.java

    @Override
    @Transactional(readOnly = true)
    public MisionProgresoResponse obtenerProgresoMision(UUID misionId, UUID profesorId) {
        log.info("Obteniendo progreso de misión: {}", misionId);

        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        // Verificar que el profesor sea el dueño
        if (!mision.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para ver este progreso");
        }

        // Obtener progreso de todos los estudiantes
        List<ProgresoMision> progresos = progresoRepository.findByMisionId(misionId);

        // Calcular estadísticas
        Long completados = progresoRepository.countCompletadosByMision(misionId);
        Long enProgreso = progresoRepository.countEnProgresoByMision(misionId);
        Long totalEstudiantes = inscripcionRepository.countEstudiantesByCurso(mision.getCurso().getId());
        Long noIniciados = totalEstudiantes - completados - enProgreso;

        // Crear respuesta de progreso por estudiante
        List<EstudianteProgresoResponse> estudiantesProgreso = progresos.stream()
                .map(progreso -> {
                    String estado = determinarEstado(progreso);
                    return EstudianteProgresoResponse.builder()
                            .estudianteId(progreso.getEstudiante().getId())
                            .nombreCompleto(progreso.getEstudiante().getNombreCompleto())
                            .avatarUrl(progreso.getEstudiante().getAvatarUrl())
                            .porcentajeCompletado(progreso.getPorcentajeCompletado())
                            .estado(estado)
                            .ultimaActividad(progreso.getUltimaActividad())
                            .build();
                })
                .collect(Collectors.toList());

        return MisionProgresoResponse.builder()
                .misionId(misionId)
                .titulo(mision.getTitulo())
                .totalEstudiantes(totalEstudiantes.intValue())
                .completados(completados.intValue())
                .enProgreso(enProgreso.intValue())
                .noIniciados(noIniciados.intValue())
                .estudiantes(estudiantesProgreso)
                .build();
    }

    @Override
    public void asignarMisionAEstudiantes(
            UUID misionId, List<UUID> estudiantesIds, UUID profesorId) {
        log.info("Asignando misión {} a {} estudiantes", misionId, estudiantesIds.size());

        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        // Verificar que el profesor sea el dueño
        if (!mision.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para asignar esta misión");
        }

        // Crear progreso y entrega para cada estudiante
        for (UUID estudianteId : estudiantesIds) {
            Usuario estudiante = usuarioRepository.findById(estudianteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

            // Verificar si ya existe progreso
            if (progresoRepository.findByMisionIdAndEstudianteId(misionId, estudianteId).isEmpty()) {
                ProgresoMision progreso = new ProgresoMision();
                progreso.setMision(mision);
                progreso.setEstudiante(estudiante);
                progreso.setPorcentajeCompletado(0);
                progreso.setCompletada(false);
                progresoRepository.save(progreso);

                // Crear entrada de entrega vacía
                EntregaMision entrega = new EntregaMision();
                entrega.setMision(mision);
                entrega.setEstudiante(estudiante);
                entrega.setEstado(EstadoEntrega.PENDIENTE);
                entregaRepository.save(entrega);

                // Crear notificación
                notificacionService.crearNotificacionNuevaMision(estudiante, mision);
            }
        }

        log.info("Misión asignada exitosamente a {} estudiantes", estudiantesIds.size());
    }

    // ========== MÉTODOS AUXILIARES PRIVADOS ==========

    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaLimite) {
        LocalDateTime ahora = LocalDateTime.now();

        // Permitir fecha de inicio desde hoy (no solo futuras)
        if (fechaInicio.toLocalDate().isBefore(ahora.toLocalDate())) {
            throw new ValidationException("La fecha de inicio no puede ser anterior a hoy");
        }

        if (fechaLimite.isBefore(fechaInicio)) {
            throw new ValidationException("La fecha límite debe ser posterior a la fecha de inicio");
        }
    }

    private void validarFechaLimite(LocalDateTime fechaLimite) {
        if (fechaLimite.isBefore(LocalDateTime.now())) {
            throw new ValidationException("La fecha límite debe ser futura");
        }
    }

    private void crearContenidos(Mision mision, List<ContenidoRequest> contenidosRequest) {
        for (ContenidoRequest req : contenidosRequest) {
            ContenidoMision contenido = new ContenidoMision();
            contenido.setMision(mision);
            contenido.setTipoContenido(req.getTipoContenido());
            contenido.setTitulo(req.getTitulo());
            contenido.setContenidoUrl(req.getContenidoUrl());
            contenido.setContenidoTexto(req.getContenidoTexto());
            contenido.setOrden(req.getOrden());
            contenidoRepository.save(contenido);
        }
    }

    private void crearCriterios(Mision mision, List<CriterioRequest> criteriosRequest) {
        for (CriterioRequest req : criteriosRequest) {
            CriteriosEvaluacion criterio = new CriteriosEvaluacion();
            criterio.setMision(mision);
            criterio.setCriterio(req.getCriterio());
            criterio.setPuntosMaximos(req.getPuntosMaximos());
            criterio.setDescripcion(req.getDescripcion());
            criterio.setOrden(req.getOrden());
            criteriosRepository.save(criterio);
        }
    }

    private void crearProgresoInicialParaEstudiantes(Mision mision) {
        UUID cursoId = mision.getCurso().getId();
        List<UUID> estudiantesIds = inscripcionRepository
                .findEstudiantesIdsByCursoId(cursoId);

        for (UUID estudianteId : estudiantesIds) {
            usuarioRepository.findById(estudianteId).ifPresent(estudiante -> {
                ProgresoMision progreso = new ProgresoMision();
                progreso.setMision(mision);
                progreso.setEstudiante(estudiante);
                progreso.setPorcentajeCompletado(0);
                progreso.setCompletada(false);
                progresoRepository.save(progreso);

                // Crear entrada de entrega vacía
                EntregaMision entrega = new EntregaMision();
                entrega.setMision(mision);
                entrega.setEstudiante(estudiante);
                entrega.setEstado(EstadoEntrega.PENDIENTE);
                entregaRepository.save(entrega);
            });
        }
    }

    private void notificarNuevaMision(Mision mision) {
        UUID cursoId = mision.getCurso().getId();
        List<UUID> estudiantesIds = inscripcionRepository
                .findEstudiantesIdsByCursoId(cursoId);

        for (UUID estudianteId : estudiantesIds) {
            usuarioRepository.findById(estudianteId).ifPresent(estudiante -> {
                notificacionService.crearNotificacionNuevaMision(estudiante, mision);
            });
        }
    }

    private String determinarEstado(ProgresoMision progreso) {
        if (progreso.getCompletada()) {
            return "completada";
        } else if (progreso.getPorcentajeCompletado() > 0) {
            return "en_progreso";
        } else {
            return "no_iniciada";
        }
    }

    private MisionResponse convertirAMisionResponse(Mision mision) {
        // Obtener contenidos
        List<ContenidoMision> contenidos = contenidoRepository
                .findByMisionIdOrderByOrdenAsc(mision.getId());

        List<ContenidoResponse> contenidosResponse = contenidos.stream()
                .map(c -> ContenidoResponse.builder()
                        .id(c.getId())
                        .tipoContenido(c.getTipoContenido())
                        .titulo(c.getTitulo())
                        .contenidoUrl(c.getContenidoUrl())
                        .contenidoTexto(c.getContenidoTexto())
                        .orden(c.getOrden())
                        .build())
                .collect(Collectors.toList());

        // Obtener criterios
        List<CriteriosEvaluacion> criterios = criteriosRepository
                .findByMisionIdOrderByOrdenAsc(mision.getId());

        List<CriterioResponse> criteriosResponse = criterios.stream()
                .map(c -> CriterioResponse.builder()
                        .id(c.getId())
                        .criterio(c.getCriterio())
                        .puntosMaximos(c.getPuntosMaximos())
                        .descripcion(c.getDescripcion())
                        .orden(c.getOrden())
                        .build())
                .collect(Collectors.toList());

        return MisionResponse.builder()
                .id(mision.getId())
                .cursoId(mision.getCurso().getId())
                .cursoNombre(mision.getCurso().getNombre())
                .profesorId(mision.getProfesor().getId())
                .profesorNombre(mision.getProfesor().getNombreCompleto())
                .titulo(mision.getTitulo())
                .descripcion(mision.getDescripcion())
                .tipoMision(mision.getTipoMision())
                .categoria(mision.getCategoria())
                .dificultad(mision.getDificultad())
                .puntosRecompensa(mision.getPuntosRecompensa())
                .experienciaRecompensa(mision.getExperienciaRecompensa())
                .fechaInicio(mision.getFechaInicio())
                .fechaLimite(mision.getFechaLimite())
                .activo(mision.getActivo())
                .requisitosPrevios(mision.getRequisitosPrevios())
                .contenidos(contenidosResponse)
                .criterios(criteriosResponse)
                .fechaCreacion(mision.getFechaCreacion())
                .fechaActualizacion(mision.getFechaActualizacion())
                .build();
    }

    private MisionListResponse convertirAMisionListResponse(Mision mision) {
        Long completados = progresoRepository.countCompletadosByMision(mision.getId());
        Long totalEstudiantes = inscripcionRepository
                .countEstudiantesByCurso(mision.getCurso().getId());

        String descripcionResumida = mision.getDescripcion().length() > 150
                ? mision.getDescripcion().substring(0, 147) + "..."
                : mision.getDescripcion();

        return MisionListResponse.builder()
                .id(mision.getId())
                .titulo(mision.getTitulo())
                .descripcionResumida(descripcionResumida)
                .categoria(mision.getCategoria())
                .dificultad(mision.getDificultad())
                .puntosRecompensa(mision.getPuntosRecompensa())
                .fechaLimite(mision.getFechaLimite())
                .activo(mision.getActivo())
                .cursoNombre(mision.getCurso().getNombre())
                .estudiantesCompletados(completados.intValue())
                .totalEstudiantes(totalEstudiantes.intValue())
                .build();
    }

    // ========== MÉTODOS PARA ESTUDIANTES ==========

    @Override
    @Transactional(readOnly = true)
    public List<MisionEstudianteResponse> listarMisionesPorEstudiante(UUID estudianteId) {
        log.info("Listando misiones del estudiante: {}", estudianteId);

        // Obtener todos los progresos del estudiante con JOIN FETCH para evitar lazy loading
        List<ProgresoMision> progresos = progresoRepository.findByEstudianteIdWithMisionAndCurso(estudianteId);

        return progresos.stream()
                .map(progreso -> {
                    Mision mision = progreso.getMision();
                    EntregaMision entrega = entregaRepository
                            .findByMisionIdAndEstudianteId(mision.getId(), estudianteId)
                            .orElse(null);

                    return MisionEstudianteResponse.builder()
                            .id(mision.getId())
                            .titulo(mision.getTitulo())
                            .descripcion(mision.getDescripcion())
                            .categoria(mision.getCategoria())
                            .dificultad(mision.getDificultad())
                            .puntosRecompensa(mision.getPuntosRecompensa())
                            .experienciaRecompensa(mision.getExperienciaRecompensa())
                            .fechaInicio(mision.getFechaInicio())
                            .fechaLimite(mision.getFechaLimite())
                            .activo(mision.getActivo())
                            .cursoNombre(mision.getCurso().getNombre())
                            .porcentajeCompletado(progreso.getPorcentajeCompletado())
                            .completada(progreso.getCompletada())
                            .fechaCompletado(progreso.getFechaCompletado())
                            .estadoEntrega(entrega != null ? entrega.getEstado() : com.eduquestia.backend.entity.enums.EstadoEntrega.PENDIENTE)
                            .puntosObtenidos(entrega != null && entrega.getPuntosObtenidos() != null ? entrega.getPuntosObtenidos() : 0)
                            .ultimaActividad(progreso.getUltimaActividad())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MisionEstudianteResponse completarMision(
            UUID misionId, CompletarMisionRequest request, UUID estudianteId) {
        log.info("Completando misión {} por estudiante {}", misionId, estudianteId);

        // Validar que la misión existe
        Mision mision = misionRepository.findById(misionId)
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada con ID: " + misionId));

        // Validar que el estudiante existe
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

        // Obtener o crear progreso
        ProgresoMision progreso = progresoRepository
                .findByMisionIdAndEstudianteId(misionId, estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("No tienes esta misión asignada"));

        // Validar que no esté ya completada
        if (progreso.getCompletada()) {
            throw new ValidationException("Esta misión ya fue completada");
        }

        // Validar fecha límite
        if (mision.getFechaLimite().isBefore(java.time.LocalDateTime.now())) {
            throw new ValidationException("La fecha límite para esta misión ha expirado");
        }

        // Actualizar progreso a 100% completado
        progreso.setPorcentajeCompletado(100);
        progreso.setCompletada(true);
        progreso.setFechaCompletado(java.time.LocalDateTime.now());
        progreso.setUltimaActividad(java.time.LocalDateTime.now());
        progresoRepository.save(progreso);

        // Obtener o crear entrega
        EntregaMision entrega = entregaRepository
                .findByMisionIdAndEstudianteId(misionId, estudianteId)
                .orElseGet(() -> {
                    EntregaMision nuevaEntrega = new EntregaMision();
                    nuevaEntrega.setMision(mision);
                    nuevaEntrega.setEstudiante(estudiante);
                    return nuevaEntrega;
                });

        // Actualizar entrega
        entrega.setEstado(com.eduquestia.backend.entity.enums.EstadoEntrega.ENVIADA);
        entrega.setContenidoEntrega(request.getContenidoEntrega());
        entrega.setArchivoUrl(request.getArchivoUrl());
        entrega.setComentariosEstudiante(request.getComentariosEstudiante());
        entrega.setFechaEnvio(java.time.LocalDateTime.now());

        // Otorgar puntos automáticamente (el profesor puede ajustarlos después)
        Integer puntosOtorgados = mision.getPuntosRecompensa();
        entrega.setPuntosObtenidos(puntosOtorgados);
        entregaRepository.save(entrega);

        log.info("Misión {} completada por estudiante {}. Puntos otorgados: {}", 
                misionId, estudianteId, puntosOtorgados);

        // Verificar y otorgar logros
        try {
            gamificacionService.verificarYOtorgarLogros(estudianteId);
        } catch (Exception e) {
            log.warn("Error al verificar logros después de completar misión: {}", e.getMessage());
            // No fallar la operación si hay error en logros
        }

        // Retornar respuesta
        return MisionEstudianteResponse.builder()
                .id(mision.getId())
                .titulo(mision.getTitulo())
                .descripcion(mision.getDescripcion())
                .categoria(mision.getCategoria())
                .dificultad(mision.getDificultad())
                .puntosRecompensa(mision.getPuntosRecompensa())
                .experienciaRecompensa(mision.getExperienciaRecompensa())
                .fechaInicio(mision.getFechaInicio())
                .fechaLimite(mision.getFechaLimite())
                .activo(mision.getActivo())
                .cursoNombre(mision.getCurso().getNombre())
                .porcentajeCompletado(100)
                .completada(true)
                .fechaCompletado(progreso.getFechaCompletado())
                .estadoEntrega(entrega.getEstado())
                .puntosObtenidos(puntosOtorgados)
                .ultimaActividad(progreso.getUltimaActividad())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerPuntosTotalesEstudiante(UUID estudianteId) {
        log.info("Obteniendo puntos totales del estudiante: {}", estudianteId);

        // Obtener todas las entregas calificadas del estudiante
        List<EntregaMision> entregas = entregaRepository.findByEstudianteId(estudianteId);

        // Sumar puntos obtenidos de todas las entregas completadas
        return entregas.stream()
                .filter(e -> e.getPuntosObtenidos() != null && e.getPuntosObtenidos() > 0)
                .mapToInt(EntregaMision::getPuntosObtenidos)
                .sum();
    }
}


