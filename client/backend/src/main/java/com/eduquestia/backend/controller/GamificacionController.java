package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.OtorgarRecompensaRequest;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingResponse;
import com.eduquestia.backend.dto.response.RecompensaManualResponse;
import com.eduquestia.backend.service.GamificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // ========== ENDPOINTS DE RECOMPENSAS MANUALES ==========
    // Historia de Usuario #12: Recompensas manuales

    /**
     * Otorgar una recompensa manual a un estudiante
     * POST /api/v1/gamificacion/recompensas/manual
     */
    @PostMapping("/recompensas/manual")
    public ResponseEntity<ApiResponse<RecompensaManualResponse>> otorgarRecompensaManual(
            @Valid @RequestBody OtorgarRecompensaRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {
        
        log.info("POST /gamificacion/recompensas/manual - Otorgar recompensa manual de {} puntos a estudiante {}",
                request.getPuntos(), request.getEstudianteId());
        
        RecompensaManualResponse recompensa = gamificacionService.otorgarRecompensaManual(request, profesorId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(recompensa, 
                        String.format("Recompensa de %d puntos otorgada exitosamente", request.getPuntos())));
    }

    /**
     * Obtener todas las recompensas manuales de un estudiante
     * GET /api/v1/gamificacion/recompensas/estudiante/{estudianteId}
     */
    @GetMapping("/recompensas/estudiante/{estudianteId}")
    public ResponseEntity<ApiResponse<List<RecompensaManualResponse>>> obtenerRecompensasPorEstudiante(
            @PathVariable UUID estudianteId) {
        
        log.info("GET /gamificacion/recompensas/estudiante/{} - Obtener recompensas del estudiante", estudianteId);
        
        List<RecompensaManualResponse> recompensas = 
                gamificacionService.obtenerRecompensasManualesPorEstudiante(estudianteId);
        
        return ResponseEntity.ok(
                ApiResponse.success(recompensas, "Recompensas obtenidas exitosamente")
        );
    }

    /**
     * Obtener todas las recompensas manuales otorgadas por un profesor
     * GET /api/v1/gamificacion/recompensas/profesor/{profesorId}
     */
    @GetMapping("/recompensas/profesor/{profesorId}")
    public ResponseEntity<ApiResponse<List<RecompensaManualResponse>>> obtenerRecompensasPorProfesor(
            @PathVariable UUID profesorId) {
        
        log.info("GET /gamificacion/recompensas/profesor/{} - Obtener recompensas otorgadas por profesor", profesorId);
        
        List<RecompensaManualResponse> recompensas = 
                gamificacionService.obtenerRecompensasManualesPorProfesor(profesorId);
        
        return ResponseEntity.ok(
                ApiResponse.success(recompensas, "Recompensas obtenidas exitosamente")
        );
    }
}

