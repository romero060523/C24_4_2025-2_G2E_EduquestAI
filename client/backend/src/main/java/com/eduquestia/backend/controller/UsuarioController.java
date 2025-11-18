package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.entity.Usuario;
import com.eduquestia.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de Usuario - SOLO CONSULTAS
 * Los usuarios son gestionados por el admin-backend (Django)
 * Este controlador solo proporciona acceso de lectura para el client-backend
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006"})
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Listar todos los estudiantes activos
     * GET /api/v1/usuarios/estudiantes
     */
    @GetMapping("/estudiantes")
    public ResponseEntity<ApiResponse<List<EstudianteSimpleDTO>>> listarEstudiantes() {
        log.info("GET /usuarios/estudiantes - Listar todos los estudiantes activos");

        List<Usuario> estudiantes = usuarioRepository.findByRolAndActivoTrue("estudiante");

        List<EstudianteSimpleDTO> estudiantesDTO = estudiantes.stream()
                .map(usuario -> EstudianteSimpleDTO.builder()
                        .id(usuario.getId().toString())
                        .nombreCompleto(usuario.getNombreCompleto())
                        .email(usuario.getEmail())
                        .username(usuario.getUsername())
                        .avatarUrl(usuario.getAvatarUrl())
                        .fechaInscripcion(usuario.getFechaCreacion() != null 
                                ? usuario.getFechaCreacion().toString() 
                                : null)
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(estudiantesDTO, "Estudiantes obtenidos exitosamente")
        );
    }

    /**
     * DTO para respuesta de estudiante simple
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EstudianteSimpleDTO {
        private String id;
        private String nombreCompleto;
        private String email;
        private String username;
        private String avatarUrl;
        private String fechaInscripcion;
    }
}

