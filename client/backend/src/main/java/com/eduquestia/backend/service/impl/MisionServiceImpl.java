package com.eduquestia.backend.service.impl;

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

    @Override
    public MisionResponse crearMision(MisionCreateRequest request, UUID profesorId) {
        log.info("Creando misión: {} para curso: {}", request.getTitulo(), request.getCursoId());

        // Validar profesor
        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado"));

        // Validar curso
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Misión no encontrada"));

        // Verificar que el profesor sea el dueño
        if (!mision.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para asignar esta misión");
        }

        // Crear progreso y entrega para cada estudiante
        for (UUID estudianteId : estudiantesIds) {
            Usuario estudiante = usuarioRepository.findById(estudianteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

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
        List<UUID> estudiantesIds = inscripcionRepository
                .findEstudiantesIdsByCursoId(mision.getCurso().getId());

        for (UUID estudianteId : estudiantesIds) {
            Usuario estudiante = usuarioRepository.findById(estudianteId).orElse(null);
            if (estudiante != null) {
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
            }
        }
    }

    private void notificarNuevaMision(Mision mision) {
        List<UUID> estudiantesIds = inscripcionRepository
                .findEstudiantesIdsByCursoId(mision.getCurso().getId());

        for (UUID estudianteId : estudiantesIds) {
            Usuario estudiante = usuarioRepository.findById(estudianteId).orElse(null);
            if (estudiante != null) {
                notificacionService.crearNotificacionNuevaMision(estudiante, mision);
            }
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
}


