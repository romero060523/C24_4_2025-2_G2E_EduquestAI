package com.eduquestia.backend.controller;

import com.eduquestia.backend.entity.Curso;
import com.eduquestia.backend.entity.Inscripcion;
import com.eduquestia.backend.repository.CursoRepository;
import com.eduquestia.backend.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador de Curso - SOLO CONSULTAS
 * Los cursos son gestionados por el admin-backend (Django)
 * Este controlador solo proporciona acceso de lectura para el client-backend
 * Para crear/editar/eliminar cursos, usar el admin-backend
 */
@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;

    /**
     * Lista los cursos de un estudiante basado en sus inscripciones
     */
    @GetMapping("/por-estudiante/{estudianteId}")
    public ResponseEntity<List<Curso>> listarCursosPorEstudiante(@PathVariable UUID estudianteId) {
        List<Inscripcion> inscripciones = inscripcionRepository.findByEstudianteId(estudianteId);
        List<Curso> cursos = inscripciones.stream()
                .map(Inscripcion::getCurso)
                .filter(Curso::getActivo)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(cursos);
    }

    /**
     * Lista todos los cursos activos
     * Por ahora devuelve todos los cursos sin filtrar por profesor
     * hasta que se implemente la relación profesor-curso
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
