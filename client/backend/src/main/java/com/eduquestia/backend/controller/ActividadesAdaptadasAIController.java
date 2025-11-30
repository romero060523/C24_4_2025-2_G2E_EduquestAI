package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.GenerarActividadesAdaptadasRequest;
import com.eduquestia.backend.dto.response.ActividadesAdaptadasResponse;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.service.ActividadesAdaptadasAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006", "http://localhost:3001"})
public class ActividadesAdaptadasAIController {

    private final ActividadesAdaptadasAIService actividadesAdaptadasAIService;

    /**
     * Generar actividades adaptadas para un curso
     * POST /api/v1/actividades-adaptadas-ai/generar
     */
    @PostMapping("/actividades-adaptadas-ai/generar")
    public ResponseEntity<ApiResponse<ActividadesAdaptadasResponse>> generarActividadesAdaptadas(
            @Valid @RequestBody GenerarActividadesAdaptadasRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /actividades-adaptadas-ai/generar - Profesor: {}, Curso: {}", 
                profesorId, request.getCursoId());

        ActividadesAdaptadasResponse response = actividadesAdaptadasAIService
                .generarActividadesAdaptadas(request, profesorId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Actividades adaptadas generadas exitosamente"));
    }
}

