package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.ConfiguracionAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaResponse;
import com.eduquestia.backend.dto.response.ConfiguracionAlertaResponse;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.entity.enums.EstadoAlerta;
import com.eduquestia.backend.entity.enums.EstadoInscripcion;
import com.eduquestia.backend.entity.enums.TipoAlerta;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.AlertaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaServiceImpl implements AlertaService {

    private final ConfiguracionAlertaRepository configuracionRepo;
    private final AlertaRendimientoRepository alertaRepo;
    private final InscripcionRepository inscripcionRepo;
    private final ProgresoMisionRepository progresoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CursoRepository cursoRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public UUID configurarAlertas(ConfiguracionAlertaRequest request, UUID profesorId) {
        Usuario profesor = usuarioRepo.findById(profesorId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Curso curso = cursoRepo.findById(request.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Desactivar configuraciones previas
        configuracionRepo.findByCursoIdAndActivoTrue(request.getCursoId())
                .ifPresent(config -> {
                    config.setActivo(false);
                    configuracionRepo.save(config);
                });

        // Crear nueva configuración
        ConfiguracionAlerta config = new ConfiguracionAlerta();
        config.setCurso(curso);
        config.setProfesor(profesor);
        config.setDiasInactividad(request.getDiasInactividad());
        config.setPorcentajeCompletitudMinimo(request.getPorcentajeCompletitudMinimo());
        config.setPuntosDebajoPromedio(request.getPuntosDebajoPromedio());
        config.setMisionesPendientesMinimo(request.getMisionesPendientesMinimo());
        config.setActivo(true);

        config = configuracionRepo.save(config);

        log.info("Configuración de alertas creada para curso {}", curso.getNombre());

        // Ejecutar evaluación inmediata
        evaluarEstudiantesCurso(config.getId());

        return config.getId();
    }

    @Override
    @Transactional
    public void evaluarEstudiantesCurso(UUID configuracionId) {
        ConfiguracionAlerta config = configuracionRepo.findById(configuracionId)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        if (!config.getActivo()) {
            return;
        }

        // Obtener estudiantes del curso
        List<Inscripcion> inscripciones = inscripcionRepo.findByCursoIdAndEstado(
                config.getCurso().getId(),
                EstadoInscripcion.ACTIVO
        );

        log.info("Evaluando {} estudiantes del curso {}",
                inscripciones.size(), config.getCurso().getNombre());

        for (Inscripcion inscripcion : inscripciones) {
            evaluarEstudiante(inscripcion.getEstudiante(), config);
        }
    }

    private void evaluarEstudiante(Usuario estudiante, ConfiguracionAlerta config) {
        // 1. Evaluar inactividad
        if (config.getDiasInactividad() != null) {
            evaluarInactividad(estudiante, config);
        }

        // 2. Evaluar completitud de misiones
        if (config.getPorcentajeCompletitudMinimo() != null) {
            evaluarCompletitud(estudiante, config);
        }

        // 3. Evaluar puntos debajo del promedio
        if (Boolean.TRUE.equals(config.getPuntosDebajoPromedio())) {
            evaluarPuntosPromedio(estudiante, config);
        }

        // 4. Evaluar misiones pendientes
        if (config.getMisionesPendientesMinimo() != null) {
            evaluarMisionesPendientes(estudiante, config);
        }
    }

    private void evaluarInactividad(Usuario estudiante, ConfiguracionAlerta config) {
        // Buscar última actividad del estudiante
        Optional<ProgresoMision> ultimoProgreso = progresoRepo
                .findTopByEstudianteIdOrderByFechaActualizacionDesc(estudiante.getId());

        LocalDateTime ultimaActividad = ultimoProgreso
                .map(ProgresoMision::getFechaActualizacion)
                .orElseGet(() -> estudiante.getFechaCreacion().toLocalDateTime());

        long diasInactivo = ChronoUnit.DAYS.between(ultimaActividad, LocalDateTime.now());

        if (diasInactivo >= config.getDiasInactividad()) {
            crearAlerta(
                    estudiante,
                    config,
                    TipoAlerta.INACTIVIDAD,
                    String.format("Sin actividad en %d días", diasInactivo),
                    Map.of("diasInactivo", diasInactivo, "ultimaActividad", ultimaActividad.toString())
            );
        }
    }

    private void evaluarCompletitud(Usuario estudiante, ConfiguracionAlerta config) {
        // Calcular % de misiones completadas
        long totalMisiones = progresoRepo.countByEstudianteIdAndCursoId(
                estudiante.getId(), config.getCurso().getId());

        long misionesCompletadas = progresoRepo.countByEstudianteIdAndCursoIdAndCompletado(
                estudiante.getId(), config.getCurso().getId(), true);

        if (totalMisiones == 0) return;

        double porcentajeCompletitud = (misionesCompletadas * 100.0) / totalMisiones;

        if (porcentajeCompletitud < config.getPorcentajeCompletitudMinimo()) {
            crearAlerta(
                    estudiante,
                    config,
                    TipoAlerta.BAJO_RENDIMIENTO,
                    String.format("Solo ha completado %.1f%% de las misiones", porcentajeCompletitud),
                    Map.of(
                            "porcentajeCompletitud", porcentajeCompletitud,
                            "misionesCompletadas", misionesCompletadas,
                            "totalMisiones", totalMisiones
                    )
            );
        }
    }

    private void evaluarPuntosPromedio(Usuario estudiante, ConfiguracionAlerta config) {
        // Obtener puntos del estudiante
        int puntosEstudiante = progresoRepo
                .sumPuntosByEstudianteIdAndCursoId(estudiante.getId(), config.getCurso().getId())
                .orElse(0);

        // Calcular promedio del curso
        Double promedioGrupo = progresoRepo.avgPuntosByCursoId(config.getCurso().getId())
                .orElse(0.0);

        if (puntosEstudiante < promedioGrupo) {
            crearAlerta(
                    estudiante,
                    config,
                    TipoAlerta.DEBAJO_PROMEDIO,
                    String.format("Puntos (%d) por debajo del promedio (%.1f)", puntosEstudiante, promedioGrupo),
                    Map.of("puntosEstudiante", puntosEstudiante, "promedioGrupo", promedioGrupo)
            );
        }
    }

    private void evaluarMisionesPendientes(Usuario estudiante, ConfiguracionAlerta config) {
        long misionesPendientes = progresoRepo.countByEstudianteIdAndCursoIdAndCompletado(
                estudiante.getId(), config.getCurso().getId(), false);

        if (misionesPendientes >= config.getMisionesPendientesMinimo()) {
            crearAlerta(
                    estudiante,
                    config,
                    TipoAlerta.MISIONES_PENDIENTES,
                    String.format("%d misiones sin completar", misionesPendientes),
                    Map.of("misionesPendientes", misionesPendientes)
            );
        }
    }

    private void crearAlerta(Usuario estudiante, ConfiguracionAlerta config,
                             TipoAlerta tipo, String descripcion, Map<String, Object> datos) {
        // Verificar si ya existe una alerta activa del mismo tipo
        boolean existeAlerta = alertaRepo.findByEstudianteIdAndEstado(estudiante.getId(), EstadoAlerta.ACTIVA)
                .stream()
                .anyMatch(a -> a.getTipo() == tipo && a.getCurso().getId().equals(config.getCurso().getId()));

        if (existeAlerta) {
            log.debug("Alerta {} ya existe para estudiante {}", tipo, estudiante.getUsername());
            return;
        }

        try {
            String datosJson = objectMapper.writeValueAsString(datos);

            AlertaRendimiento alerta = new AlertaRendimiento();
            alerta.setEstudiante(estudiante);
            alerta.setCurso(config.getCurso());
            alerta.setConfiguracion(config);
            alerta.setTipo(tipo);
            alerta.setDescripcion(descripcion);
            alerta.setDatosContexto(datosJson);
            alerta.setEstado(EstadoAlerta.ACTIVA);

            alertaRepo.save(alerta);

            log.info("Alerta {} creada para estudiante {} en curso {}",
                    tipo, estudiante.getUsername(), config.getCurso().getNombre());
        } catch (Exception e) {
            log.error("Error al crear alerta", e);
        }
    }

    @Override
    public List<AlertaResponse> obtenerAlertasActivasPorCurso(UUID cursoId) {
        return alertaRepo.findAlertasActivasPorCurso(cursoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolverAlerta(UUID alertaId) {
        AlertaRendimiento alerta = alertaRepo.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alerta.setEstado(EstadoAlerta.RESUELTA);
        alerta.setFechaResolucion(LocalDateTime.now());
        alertaRepo.save(alerta);
    }

    private AlertaResponse mapToResponse(AlertaRendimiento alerta) {
        return AlertaResponse.builder()
                .id(alerta.getId())
                .estudianteId(alerta.getEstudiante().getId())
                .estudianteNombre(alerta.getEstudiante().getNombreCompleto())
                .estudianteEmail(alerta.getEstudiante().getEmail())
                .cursoId(alerta.getCurso().getId())
                .cursoNombre(alerta.getCurso().getNombre())
                .tipo(alerta.getTipo())
                .descripcion(alerta.getDescripcion())
                .datosContexto(alerta.getDatosContexto())
                .estado(alerta.getEstado())
                .fechaCreacion(alerta.getFechaCreacion())
                .build();
    }

    @Override
    public List<AlertaResponse> obtenerAlertasActivasPorEstudiante(UUID estudianteId) {
        return alertaRepo.findByEstudianteIdAndEstado(estudianteId, EstadoAlerta.ACTIVA)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void ignorarAlerta(UUID alertaId) {
        AlertaRendimiento alerta = alertaRepo.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alerta.setEstado(EstadoAlerta.IGNORADA);
        alerta.setFechaResolucion(LocalDateTime.now());
        alertaRepo.save(alerta);
    }

    @Override
    @Transactional
    public void evaluarCursoManualmente(UUID cursoId) {
        ConfiguracionAlerta config = configuracionRepo.findByCursoIdAndActivoTrue(cursoId)
                .orElseThrow(() -> new RuntimeException("No hay configuración de alertas para este curso"));

        evaluarEstudiantesCurso(config.getId());
    }

    @Override
    public ConfiguracionAlertaResponse obtenerConfiguracionPorCurso(UUID cursoId) {
        ConfiguracionAlerta config = configuracionRepo.findByCursoIdAndActivoTrue(cursoId)
                .orElse(null);

        if (config == null) {
            return null;
        }

        return ConfiguracionAlertaResponse.builder()
                .id(config.getId())
                .cursoId(config.getCurso().getId())
                .cursoNombre(config.getCurso().getNombre())
                .diasInactividad(config.getDiasInactividad())
                .porcentajeCompletitudMinimo(config.getPorcentajeCompletitudMinimo())
                .puntosDebajoPromedio(config.getPuntosDebajoPromedio())
                .misionesPendientesMinimo(config.getMisionesPendientesMinimo())
                .activo(config.getActivo())
                .fechaCreacion(config.getFechaCreacion())
                .build();
    }

}
