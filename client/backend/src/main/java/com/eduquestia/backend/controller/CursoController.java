package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.CursoEstudianteResponse;
import com.eduquestia.backend.entity.Curso;
import com.eduquestia.backend.entity.CursoProfesor;
import com.eduquestia.backend.entity.Inscripcion;
import com.eduquestia.backend.entity.Mision;
import com.eduquestia.backend.entity.ProgresoMision;
import com.eduquestia.backend.repository.CursoProfesorRepository;
import com.eduquestia.backend.repository.CursoRepository;
import com.eduquestia.backend.repository.InscripcionRepository;
import com.eduquestia.backend.repository.MisionRepository;
import com.eduquestia.backend.repository.ProgresoMisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador de Curso - SOLO CONSULTAS
 * Los cursos son gestionados por el admin-backend (Django)
 * Este controlador solo proporciona acceso de lectura para el client-backend
 */
@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CursoProfesorRepository cursoProfesorRepository;
    private final MisionRepository misionRepository;
    private final ProgresoMisionRepository progresoMisionRepository;

    /**
     * Lista los cursos de un estudiante con información completa
     */
    @GetMapping("/por-estudiante/{estudianteId}")
    public ResponseEntity<ApiResponse<List<CursoEstudianteResponse>>> listarCursosPorEstudiante(
            @PathVariable UUID estudianteId) {

        log.info("GET /cursos/por-estudiante/{} - Listar cursos del estudiante", estudianteId);

        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteIdWithCurso(estudianteId);
        List<CursoEstudianteResponse> cursosResponse = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            Curso curso = inscripcion.getCurso();
            if (curso == null || !curso.getActivo()) continue;

            // Obtener profesor titular
            String profesorNombre = "Sin asignar";
            String profesorEmail = "";
            List<CursoProfesor> profesores = cursoProfesorRepository.findByCursoId(curso.getId());
            if (!profesores.isEmpty()) {
                // Buscar el titular primero
                CursoProfesor titular = profesores.stream()
                        .filter(cp -> "titular".equalsIgnoreCase(cp.getRolProfesor()))
                        .findFirst()
                        .orElse(profesores.get(0));

                if (titular.getProfesor() != null) {
                    profesorNombre = titular.getProfesor().getNombreCompleto();
                    profesorEmail = titular.getProfesor().getEmail();
                }
            }

            // Contar misiones del curso
            List<Mision> misionesDelCurso = misionRepository.findByCursoId(curso.getId());
            int totalMisiones = misionesDelCurso.size();

            // Contar misiones completadas por el estudiante
            int misionesCompletadas = 0;
            for (Mision mision : misionesDelCurso) {
                var progresoOpt = progresoMisionRepository
                        .findByMisionIdAndEstudianteId(mision.getId(), estudianteId);
                if (progresoOpt.isPresent() && progresoOpt.get().getCompletada() != null && progresoOpt.get().getCompletada()) {
                    misionesCompletadas++;
                }
            }

            // Calcular progreso
            int progreso = totalMisiones > 0 ? (misionesCompletadas * 100) / totalMisiones : 0;

            // Contar estudiantes inscritos
            int totalEstudiantes = inscripcionRepository.findInscripcionesActivasByCursoId(curso.getId()).size();

            CursoEstudianteResponse cursoResponse = CursoEstudianteResponse.builder()
                    .id(curso.getId())
                    .codigoCurso(curso.getCodigoCurso())
                    .nombre(curso.getNombre())
                    .descripcion(curso.getDescripcion())
                    .imagenPortada(curso.getImagenPortada())
                    .fechaInicio(curso.getFechaInicio() != null ? curso.getFechaInicio().toString() : null)
                    .fechaFin(curso.getFechaFin() != null ? curso.getFechaFin().toString() : null)
                    .activo(curso.getActivo())
                    .profesorNombre(profesorNombre)
                    .profesorEmail(profesorEmail)
                    .progreso(progreso)
                    .misionesCompletadas(misionesCompletadas)
                    .totalMisiones(totalMisiones)
                    .totalEstudiantes(totalEstudiantes)
                    .build();

            cursosResponse.add(cursoResponse);
        }

        return ResponseEntity.ok(ApiResponse.success(cursosResponse, "Cursos obtenidos exitosamente"));
    }

    /**
     * Lista todos los cursos activos
     */
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<Map<String, Object>> listarCursosPorProfesor(@PathVariable("profesorId") String profesorId) {
        List<Curso> cursos = cursoRepository.findByActivoTrue();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", cursos);
        response.put("message", "Cursos obtenidos exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los cursos activos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarCursos() {
        List<Curso> cursos = cursoRepository.findByActivoTrue();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", cursos);
        response.put("message", "Cursos obtenidos exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Lista los estudiantes inscritos en un curso
     * IMPORTANTE: Este endpoint debe ir ANTES de /{id} para evitar conflictos de rutas
     */
    @GetMapping("/{cursoId}/estudiantes")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> listarEstudiantesPorCurso(@PathVariable("cursoId") UUID cursoId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByCursoId(cursoId);

        List<Map<String, Object>> estudiantes = inscripciones.stream()
                .filter(i -> i.getEstudiante() != null && i.getEstudiante().getActivo())
                .map(i -> {
                    Map<String, Object> est = new HashMap<>();
                    est.put("id", i.getEstudiante().getId().toString());
                    est.put("nombreCompleto", i.getEstudiante().getNombreCompleto());
                    est.put("email", i.getEstudiante().getEmail());
                    return est;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", estudiantes);
        response.put("message", "Estudiantes obtenidos exitosamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un curso por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCursoPorId(@PathVariable @NonNull UUID id) {
        return cursoRepository.findById(id)
            .map(curso -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", curso);
                response.put("message", "Curso encontrado");
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Curso no encontrado");
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Obtiene un curso por su código
     */
    @GetMapping("/codigo/{codigoCurso}")
    public ResponseEntity<Map<String, Object>> obtenerCursoPorCodigo(@PathVariable("codigoCurso") String codigoCurso) {
        return cursoRepository.findByCodigoCurso(codigoCurso)
            .map(curso -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", curso);
                response.put("message", "Curso encontrado");
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Curso no encontrado");
                return ResponseEntity.notFound().build();
            });
    }
}
