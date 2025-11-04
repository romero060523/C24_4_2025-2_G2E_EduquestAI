package com.eduquestia.backend.controller;

import com.eduquestia.backend.entity.Curso;
import com.eduquestia.backend.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Lista todos los cursos activos
     * Por ahora devuelve todos los cursos sin filtrar por profesor
     * hasta que se implemente la relación profesor-curso
     */
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<Map<String, Object>> listarCursosPorProfesor(@PathVariable String profesorId) {
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
     * Obtiene un curso por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCursoPorId(@PathVariable UUID id) {
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
    public ResponseEntity<Map<String, Object>> obtenerCursoPorCodigo(@PathVariable String codigoCurso) {
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
