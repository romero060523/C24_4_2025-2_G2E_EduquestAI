package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.GenerarRetroalimentacionRequest;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.RetroalimentacionResponse;
import com.eduquestia.backend.service.RetroalimentacionAIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/retroalimentacion-ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006", "http://localhost:3001"})
public class RetroalimentacionAIController {

    private final RetroalimentacionAIService retroalimentacionAIService;

    /**
     * Generar retroalimentación automática para un estudiante
     * POST /api/v1/retroalimentacion-ai/generar
     */
    @PostMapping("/generar")
    public ResponseEntity<ApiResponse<RetroalimentacionResponse>> generarRetroalimentacion(
            @Valid @RequestBody GenerarRetroalimentacionRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /retroalimentacion-ai/generar - Profesor: {}, Estudiante: {}, Evaluación: {}", 
                profesorId, request.getEstudianteId(), request.getEvaluacionId());

        RetroalimentacionResponse response = retroalimentacionAIService
                .generarRetroalimentacion(request, profesorId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Retroalimentación generada exitosamente"));
    }
}

