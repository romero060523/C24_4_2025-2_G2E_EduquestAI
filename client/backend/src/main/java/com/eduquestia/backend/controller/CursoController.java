package com.eduquestia.backend.controller;

import com.eduquestia.backend.entity.Curso;
import com.eduquestia.backend.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
