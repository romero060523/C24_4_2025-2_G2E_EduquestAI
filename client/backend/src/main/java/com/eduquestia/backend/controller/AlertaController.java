package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.ConfiguracionAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaResponse;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.ConfiguracionAlertaResponse;
import com.eduquestia.backend.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping("/configurar")
    public ResponseEntity<ApiResponse<UUID>> configurarAlertas(
            @Valid @RequestBody ConfiguracionAlertaRequest request,
            @RequestHeader("X-Profesor-Id") String profesorId) {

        log.info("POST /alertas/configurar - Curso: {}, Profesor: {}",
                request.getCursoId(), profesorId);

        UUID configId = alertaService.configurarAlertas(request, UUID.fromString(profesorId));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(configId, "Configuración de alertas creada exitosamente"));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> obtenerAlertasCurso(
            @PathVariable UUID cursoId) {

        log.info("GET /alertas/curso/{}", cursoId);

        List<AlertaResponse> alertas = alertaService.obtenerAlertasActivasPorCurso(cursoId);

        return ResponseEntity.ok(
                ApiResponse.success(alertas,
                        String.format("Se encontraron %d alertas activas", alertas.size()))
        );
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> obtenerAlertasEstudiante(
            @PathVariable UUID estudianteId) {

        log.info("GET /alertas/estudiante/{}", estudianteId);

        List<AlertaResponse> alertas = alertaService.obtenerAlertasActivasPorEstudiante(estudianteId);

        return ResponseEntity.ok(ApiResponse.success(alertas, "Alertas del estudiante"));
    }

    @PatchMapping("/{alertaId}/resolver")
    public ResponseEntity<ApiResponse<Void>> resolverAlerta(@PathVariable UUID alertaId) {

        log.info("PATCH /alertas/{}/resolver", alertaId);

        alertaService.resolverAlerta(alertaId);

        return ResponseEntity.ok(ApiResponse.success(null, "Alerta marcada como resuelta"));
    }

    @PatchMapping("/{alertaId}/ignorar")
    public ResponseEntity<ApiResponse<Void>> ignorarAlerta(@PathVariable UUID alertaId) {

        log.info("PATCH /alertas/{}/ignorar", alertaId);

        alertaService.ignorarAlerta(alertaId);

        return ResponseEntity.ok(ApiResponse.success(null, "Alerta ignorada"));
    }

    @PostMapping("/evaluar/{cursoId}")
    public ResponseEntity<ApiResponse<Void>> evaluarCursoManualmente(@PathVariable UUID cursoId) {

        log.info("POST /alertas/evaluar/{}", cursoId);

        alertaService.evaluarCursoManualmente(cursoId);

        return ResponseEntity.ok(ApiResponse.success(null, "Evaluación iniciada"));
    }

    @GetMapping("/configuracion/curso/{cursoId}")
    public ResponseEntity<ApiResponse<ConfiguracionAlertaResponse>> obtenerConfiguracion(
            @PathVariable UUID cursoId) {

        log.info("GET /alertas/configuracion/curso/{}", cursoId);

        ConfiguracionAlertaResponse config = alertaService.obtenerConfiguracionPorCurso(cursoId);

        return ResponseEntity.ok(ApiResponse.success(config, "Configuración obtenida"));
    }
}
