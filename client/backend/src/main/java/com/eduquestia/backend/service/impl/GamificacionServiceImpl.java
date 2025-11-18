package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.OtorgarRecompensaRequest;
import com.eduquestia.backend.dto.response.LogroResponse;
import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingEstudianteResponse;
import com.eduquestia.backend.dto.response.RankingResponse;
import com.eduquestia.backend.dto.response.RecompensaManualResponse;
import com.eduquestia.backend.entity.*;
import com.eduquestia.backend.exceptions.ResourceNotFoundException;
import com.eduquestia.backend.exceptions.UnauthorizedException;
import com.eduquestia.backend.exceptions.ValidationException;
import com.eduquestia.backend.repository.*;
import com.eduquestia.backend.service.GamificacionService;
import com.eduquestia.backend.service.MisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GamificacionServiceImpl implements GamificacionService {

    private final UsuarioRepository usuarioRepository;
    private final ProgresoMisionRepository progresoRepository;
    private final LogroRepository logroRepository;
    private final LogroEstudianteRepository logroEstudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final MisionService misionService;
    private final CursoRepository cursoRepository;
    private final RecompensaManualRepository recompensaManualRepository;

    @Override
    @Transactional(readOnly = true)
    public PerfilGamificadoResponse obtenerPerfilGamificado(UUID estudianteId) {
        log.info("Obteniendo perfil gamificado para estudiante: {}", estudianteId);

        // Obtener puntos totales (misiones + recompensas manuales)
        Integer puntosMisiones = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
        Integer puntosRecompensas = obtenerPuntosRecompensasManuales(estudianteId);
        Integer puntosTotales = puntosMisiones + puntosRecompensas;

        // Calcular nivel
        Integer nivel = calcularNivel(puntosTotales);
        String nombreNivel = obtenerNombreNivel(nivel);
        Integer puntosParaSiguiente = calcularPuntosParaSiguienteNivel(puntosTotales);

        // Contar misiones completadas
        List<ProgresoMision> progresos = progresoRepository.findByEstudianteId(estudianteId);
        Integer misionesCompletadas = (int) progresos.stream()
                .filter(ProgresoMision::getCompletada)
                .count();

        // Obtener logros del estudiante
        List<LogroEstudiante> logrosObtenidos = logroEstudianteRepository.findByEstudianteId(estudianteId);
        Integer logrosObtenidosCount = logrosObtenidos.size();

        // Obtener todos los logros disponibles
        List<Logro> todosLosLogros = logroRepository.findByActivoTrueOrderByPuntosRequeridosAsc();
        
        // Crear lista de logros con información de obtención
        List<LogroResponse> logrosResponse = todosLosLogros.stream()
                .map(logro -> {
                    Optional<LogroEstudiante> logroEstudiante = logrosObtenidos.stream()
                            .filter(le -> le.getLogro().getId().equals(logro.getId()))
                            .findFirst();

                    return LogroResponse.builder()
                            .id(logro.getId())
                            .nombre(logro.getNombre())
                            .descripcion(logro.getDescripcion())
                            .icono(logro.getIcono())
                            .puntosRequeridos(logro.getPuntosRequeridos())
                            .fechaObtenido(logroEstudiante.map(LogroEstudiante::getFechaObtenido).orElse(null))
                            .obtenido(logroEstudiante.isPresent())
                            .build();
                })
                .collect(Collectors.toList());

        // Calcular posición en ranking global (opcional, puede ser costoso)
        Integer posicionRanking = null; // Se puede calcular si es necesario

        return PerfilGamificadoResponse.builder()
                .puntosTotales(puntosTotales)
                .nivel(nivel)
                .nombreNivel(nombreNivel)
                .puntosParaSiguienteNivel(puntosParaSiguiente)
                .misionesCompletadas(misionesCompletadas)
                .logrosObtenidos(logrosObtenidosCount)
                .logros(logrosResponse)
                .posicionRanking(posicionRanking)
                .build();
    }

    @Override
    public void verificarYOtorgarLogros(UUID estudianteId) {
        log.info("Verificando logros para estudiante: {}", estudianteId);

        Integer puntosMisiones = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
        Integer puntosRecompensas = obtenerPuntosRecompensasManuales(estudianteId);
        Integer puntosTotales = puntosMisiones + puntosRecompensas;
        List<ProgresoMision> progresos = progresoRepository.findByEstudianteId(estudianteId);
        Integer misionesCompletadas = (int) progresos.stream()
                .filter(ProgresoMision::getCompletada)
                .count();

        // Obtener todos los logros que aún no ha obtenido
        List<LogroEstudiante> logrosObtenidos = logroEstudianteRepository.findByEstudianteId(estudianteId);
        Set<UUID> logrosObtenidosIds = logrosObtenidos.stream()
                .map(le -> le.getLogro().getId())
                .collect(Collectors.toSet());

        List<Logro> logrosDisponibles = logroRepository.findByActivoTrueOrderByPuntosRequeridosAsc();

        java.util.Objects.requireNonNull(estudianteId, "ID de estudiante no puede ser null");
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Integer nivel = calcularNivel(puntosTotales);

        for (Logro logro : logrosDisponibles) {
            // Si ya lo tiene, saltar
            if (logrosObtenidosIds.contains(logro.getId())) {
                continue;
            }

            boolean cumpleRequisitos = true;

            // Verificar puntos requeridos
            if (logro.getPuntosRequeridos() != null && puntosTotales < logro.getPuntosRequeridos()) {
                cumpleRequisitos = false;
            }

            // Verificar nivel requerido
            if (logro.getNivelRequerido() != null && nivel < logro.getNivelRequerido()) {
                cumpleRequisitos = false;
            }

            // Verificar misiones completadas requeridas
            if (logro.getMisionesCompletadasRequeridas() != null && 
                misionesCompletadas < logro.getMisionesCompletadasRequeridas()) {
                cumpleRequisitos = false;
            }

            if (cumpleRequisitos) {
                // Otorgar logro
                LogroEstudiante logroEstudiante = new LogroEstudiante();
                logroEstudiante.setEstudiante(estudiante);
                logroEstudiante.setLogro(logro);
                logroEstudiante.setFechaObtenido(LocalDateTime.now());
                
                logroEstudianteRepository.save(logroEstudiante);
                log.info("Logro '{}' otorgado a estudiante {}", logro.getNombre(), estudianteId);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse obtenerRankingPorCurso(UUID cursoId) {
        log.info("Obteniendo ranking para curso: {}", cursoId);

        java.util.Objects.requireNonNull(cursoId, "ID de curso no puede ser null");
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Obtener todas las inscripciones activas del curso
        List<Inscripcion> inscripciones = inscripcionRepository.findByCursoId(cursoId)
                .stream()
                .filter(i -> "activo".equals(i.getEstado()))
                .collect(Collectors.toList());

        List<RankingEstudianteResponse> rankingEstudiantes = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            UUID estudianteId = inscripcion.getEstudiante().getId();
            
            Integer puntosMisiones = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
        Integer puntosRecompensas = obtenerPuntosRecompensasManuales(estudianteId);
        Integer puntosTotales = puntosMisiones + puntosRecompensas;
            Integer nivel = calcularNivel(puntosTotales);
            
            List<ProgresoMision> progresos = progresoRepository.findByEstudianteId(estudianteId);
            Integer misionesCompletadas = (int) progresos.stream()
                    .filter(ProgresoMision::getCompletada)
                    .count();

            rankingEstudiantes.add(RankingEstudianteResponse.builder()
                    .estudianteId(estudianteId)
                    .nombreEstudiante(inscripcion.getEstudiante().getNombreCompleto())
                    .puntosTotales(puntosTotales)
                    .nivel(nivel)
                    .nombreNivel(obtenerNombreNivel(nivel))
                    .misionesCompletadas(misionesCompletadas)
                    .posicion(0) // Se establecerá después
                    .build());
        }

        // Ordenar por puntos (descendente) y luego por misiones completadas
        rankingEstudiantes.sort((a, b) -> {
            int comparacion = b.getPuntosTotales().compareTo(a.getPuntosTotales());
            if (comparacion == 0) {
                comparacion = b.getMisionesCompletadas().compareTo(a.getMisionesCompletadas());
            }
            return comparacion;
        });

        // Asignar posiciones
        for (int i = 0; i < rankingEstudiantes.size(); i++) {
            rankingEstudiantes.get(i).setPosicion(i + 1);
        }

        return RankingResponse.builder()
                .cursoId(cursoId)
                .cursoNombre(curso.getNombre())
                .estudiantes(rankingEstudiantes)
                .totalEstudiantes(rankingEstudiantes.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse obtenerRankingGlobal() {
        log.info("Obteniendo ranking global");

        // Obtener todos los estudiantes activos
        List<Usuario> estudiantes = usuarioRepository.findByRolAndActivoTrue("estudiante");

        List<RankingEstudianteResponse> rankingEstudiantes = new ArrayList<>();

        for (Usuario estudiante : estudiantes) {
            UUID estudianteId = estudiante.getId();
            
            Integer puntosMisiones = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
        Integer puntosRecompensas = obtenerPuntosRecompensasManuales(estudianteId);
        Integer puntosTotales = puntosMisiones + puntosRecompensas;
            Integer nivel = calcularNivel(puntosTotales);
            
            List<ProgresoMision> progresos = progresoRepository.findByEstudianteId(estudianteId);
            Integer misionesCompletadas = (int) progresos.stream()
                    .filter(ProgresoMision::getCompletada)
                    .count();

            rankingEstudiantes.add(RankingEstudianteResponse.builder()
                    .estudianteId(estudianteId)
                    .nombreEstudiante(estudiante.getNombreCompleto())
                    .puntosTotales(puntosTotales)
                    .nivel(nivel)
                    .nombreNivel(obtenerNombreNivel(nivel))
                    .misionesCompletadas(misionesCompletadas)
                    .posicion(0) // Se establecerá después
                    .build());
        }

        // Ordenar por puntos (descendente)
        rankingEstudiantes.sort((a, b) -> {
            int comparacion = b.getPuntosTotales().compareTo(a.getPuntosTotales());
            if (comparacion == 0) {
                comparacion = b.getMisionesCompletadas().compareTo(a.getMisionesCompletadas());
            }
            return comparacion;
        });

        // Asignar posiciones
        for (int i = 0; i < rankingEstudiantes.size(); i++) {
            rankingEstudiantes.get(i).setPosicion(i + 1);
        }

        return RankingResponse.builder()
                .cursoId(null)
                .cursoNombre("Ranking Global")
                .estudiantes(rankingEstudiantes)
                .totalEstudiantes(rankingEstudiantes.size())
                .build();
    }

    @Override
    public Integer calcularNivel(Integer puntos) {
        if (puntos == null || puntos < 0) return 1;
        
        // Sistema de niveles basado en puntos (similar al frontend)
        if (puntos < 100) return 1;      // Principiante
        if (puntos < 500) return 2;      // Principiante+
        if (puntos < 1000) return 3;    // Intermedio
        if (puntos < 2500) return 4;    // Avanzado
        if (puntos < 5000) return 5;    // Experto
        return 6;                        // Maestro
    }

    @Override
    public String obtenerNombreNivel(Integer nivel) {
        if (nivel == null) return "Principiante";
        
        switch (nivel) {
            case 1: return "Principiante";
            case 2: return "Principiante+";
            case 3: return "Intermedio";
            case 4: return "Avanzado";
            case 5: return "Experto";
            case 6: return "Maestro";
            default: return "Principiante";
        }
    }

    @Override
    public Integer calcularPuntosParaSiguienteNivel(Integer puntos) {
        if (puntos == null || puntos < 0) return 100;
        
        if (puntos < 100) return 100 - puntos;
        if (puntos < 500) return 500 - puntos;
        if (puntos < 1000) return 1000 - puntos;
        if (puntos < 2500) return 2500 - puntos;
        if (puntos < 5000) return 5000 - puntos;
        return 0; // Ya es el nivel máximo
    }

    // ========== MÉTODOS DE RECOMPENSAS MANUALES ==========
    // Historia de Usuario #12: Recompensas manuales

    @Override
    public RecompensaManualResponse otorgarRecompensaManual(
            OtorgarRecompensaRequest request, UUID profesorId) {
        log.info("Otorgando recompensa manual de {} puntos a estudiante {} por profesor {}",
                request.getPuntos(), request.getEstudianteId(), profesorId);

        // Validar profesor
        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + profesorId));

        if (!"profesor".equals(profesor.getRol())) {
            throw new UnauthorizedException("Solo los profesores pueden otorgar recompensas manuales");
        }

        // Validar estudiante
        Usuario estudiante = usuarioRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getEstudianteId()));

        if (!"estudiante".equals(estudiante.getRol())) {
            throw new ValidationException("El usuario especificado no es un estudiante");
        }

        // Validar curso
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.getCursoId()));

        // Crear recompensa manual
        RecompensaManual recompensa = new RecompensaManual();
        recompensa.setProfesor(profesor);
        recompensa.setEstudiante(estudiante);
        recompensa.setCurso(curso);
        recompensa.setPuntos(request.getPuntos());
        recompensa.setMotivo(request.getMotivo());
        recompensa.setObservaciones(request.getObservaciones());

        recompensa = recompensaManualRepository.save(recompensa);

        log.info("Recompensa manual otorgada exitosamente. ID: {}", recompensa.getId());

        // Verificar y otorgar logros después de otorgar recompensa
        try {
            verificarYOtorgarLogros(estudiante.getId());
        } catch (Exception e) {
            log.warn("Error al verificar logros después de otorgar recompensa: {}", e.getMessage());
        }

        // Retornar respuesta
        return RecompensaManualResponse.builder()
                .id(recompensa.getId())
                .profesorId(profesor.getId())
                .profesorNombre(profesor.getNombreCompleto())
                .estudianteId(estudiante.getId())
                .estudianteNombre(estudiante.getNombreCompleto())
                .cursoId(curso.getId())
                .cursoNombre(curso.getNombre())
                .puntos(recompensa.getPuntos())
                .motivo(recompensa.getMotivo())
                .observaciones(recompensa.getObservaciones())
                .fechaOtorgamiento(recompensa.getFechaOtorgamiento())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecompensaManualResponse> obtenerRecompensasManualesPorEstudiante(UUID estudianteId) {
        log.info("Obteniendo recompensas manuales del estudiante: {}", estudianteId);

        List<RecompensaManual> recompensas = recompensaManualRepository
                .findByEstudianteIdOrderByFechaOtorgamientoDesc(estudianteId);

        return recompensas.stream()
                .map(r -> RecompensaManualResponse.builder()
                        .id(r.getId())
                        .profesorId(r.getProfesor().getId())
                        .profesorNombre(r.getProfesor().getNombreCompleto())
                        .estudianteId(r.getEstudiante().getId())
                        .estudianteNombre(r.getEstudiante().getNombreCompleto())
                        .cursoId(r.getCurso() != null ? r.getCurso().getId() : null)
                        .cursoNombre(r.getCurso() != null ? r.getCurso().getNombre() : null)
                        .puntos(r.getPuntos())
                        .motivo(r.getMotivo())
                        .observaciones(r.getObservaciones())
                        .fechaOtorgamiento(r.getFechaOtorgamiento())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecompensaManualResponse> obtenerRecompensasManualesPorProfesor(UUID profesorId) {
        log.info("Obteniendo recompensas manuales otorgadas por profesor: {}", profesorId);

        List<RecompensaManual> recompensas = recompensaManualRepository
                .findByProfesorIdOrderByFechaOtorgamientoDesc(profesorId);

        return recompensas.stream()
                .map(r -> RecompensaManualResponse.builder()
                        .id(r.getId())
                        .profesorId(r.getProfesor().getId())
                        .profesorNombre(r.getProfesor().getNombreCompleto())
                        .estudianteId(r.getEstudiante().getId())
                        .estudianteNombre(r.getEstudiante().getNombreCompleto())
                        .cursoId(r.getCurso() != null ? r.getCurso().getId() : null)
                        .cursoNombre(r.getCurso() != null ? r.getCurso().getNombre() : null)
                        .puntos(r.getPuntos())
                        .motivo(r.getMotivo())
                        .observaciones(r.getObservaciones())
                        .fechaOtorgamiento(r.getFechaOtorgamiento())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerPuntosRecompensasManuales(UUID estudianteId) {
        Integer puntos = recompensaManualRepository.sumPuntosByEstudianteId(estudianteId);
        return puntos != null ? puntos : 0;
    }
}

