package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingResponse;
import com.eduquestia.backend.service.GamificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/gamificacion")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006"})
public class GamificacionController {

    private final GamificacionService gamificacionService;

    /**
     * Obtener perfil gamificado del estudiante
     * GET /api/v1/gamificacion/estudiante/{estudianteId}/perfil
     */
    @GetMapping("/estudiante/{estudianteId}/perfil")
    public ResponseEntity<ApiResponse<PerfilGamificadoResponse>> obtenerPerfilGamificado(
            @PathVariable UUID estudianteId) {
        
        log.info("GET /gamificacion/estudiante/{}/perfil - Obtener perfil gamificado", estudianteId);
        
        PerfilGamificadoResponse perfil = gamificacionService.obtenerPerfilGamificado(estudianteId);
        
        return ResponseEntity.ok(
                ApiResponse.success(perfil, "Perfil gamificado obtenido exitosamente")
        );
    }

    /**
     * Obtener ranking por curso
     * GET /api/v1/gamificacion/ranking/curso/{cursoId}
     */
    @GetMapping("/ranking/curso/{cursoId}")
    public ResponseEntity<ApiResponse<RankingResponse>> obtenerRankingPorCurso(
            @PathVariable UUID cursoId) {
        
        log.info("GET /gamificacion/ranking/curso/{} - Obtener ranking", cursoId);
        
        RankingResponse ranking = gamificacionService.obtenerRankingPorCurso(cursoId);
        
        return ResponseEntity.ok(
                ApiResponse.success(ranking, "Ranking obtenido exitosamente")
        );
    }

    /**
     * Obtener ranking global
     * GET /api/v1/gamificacion/ranking/global
     */
    @GetMapping("/ranking/global")
    public ResponseEntity<ApiResponse<RankingResponse>> obtenerRankingGlobal() {
        
        log.info("GET /gamificacion/ranking/global - Obtener ranking global");
        
        RankingResponse ranking = gamificacionService.obtenerRankingGlobal();
        
        return ResponseEntity.ok(
                ApiResponse.success(ranking, "Ranking global obtenido exitosamente")
        );
    }
}

