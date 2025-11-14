package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.response.LogroResponse;
import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingEstudianteResponse;
import com.eduquestia.backend.dto.response.RankingResponse;
import com.eduquestia.backend.entity.*;
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

    @Override
    @Transactional(readOnly = true)
    public PerfilGamificadoResponse obtenerPerfilGamificado(UUID estudianteId) {
        log.info("Obteniendo perfil gamificado para estudiante: {}", estudianteId);

        // Obtener puntos totales
        Integer puntosTotales = misionService.obtenerPuntosTotalesEstudiante(estudianteId);

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

        Integer puntosTotales = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
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
            
            Integer puntosTotales = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
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
            
            Integer puntosTotales = misionService.obtenerPuntosTotalesEstudiante(estudianteId);
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
}

