package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.ActualizarAlertaRequest;
import com.eduquestia.backend.dto.request.CrearAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaTempranaResponse;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.service.AlertaTempranaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alertas-temprana")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006"})
public class AlertaTempranaController {

    private final AlertaTempranaService alertaService;

    /**
     * Crear una nueva alerta temprana
     * POST /alertas-temprana
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AlertaTempranaResponse>> crearAlerta(
            @Valid @RequestBody CrearAlertaRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /alertas-temprana - Crear alerta para estudiante: {}", request.getEstudianteId());

        AlertaTempranaResponse response = alertaService.crearAlerta(request, profesorId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Alerta creada exitosamente"));
    }

    /**
     * Actualizar una alerta temprana
     * PUT /alertas-temprana/{alertaId}
     */
    @PutMapping("/{alertaId}")
    public ResponseEntity<ApiResponse<AlertaTempranaResponse>> actualizarAlerta(
            @PathVariable UUID alertaId,
            @Valid @RequestBody ActualizarAlertaRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("PUT /alertas-temprana/{} - Actualizar alerta", alertaId);

        AlertaTempranaResponse response = alertaService.actualizarAlerta(alertaId, request, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Alerta actualizada exitosamente")
        );
    }

    /**
     * Obtener alertas de un estudiante
     * GET /alertas-temprana/estudiante/{estudianteId}
     */
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<ApiResponse<List<AlertaTempranaResponse>>> obtenerAlertasPorEstudiante(
            @PathVariable UUID estudianteId) {

        log.info("GET /alertas-temprana/estudiante/{} - Obtener alertas", estudianteId);

        List<AlertaTempranaResponse> response = alertaService.obtenerAlertasPorEstudiante(estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Alertas obtenidas exitosamente")
        );
    }

    /**
     * Obtener alertas creadas por un profesor
     * GET /alertas-temprana/profesor/{profesorId}
     */
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<ApiResponse<List<AlertaTempranaResponse>>> obtenerAlertasPorProfesor(
            @PathVariable UUID profesorId) {

        log.info("GET /alertas-temprana/profesor/{} - Obtener alertas", profesorId);

        List<AlertaTempranaResponse> response = alertaService.obtenerAlertasPorProfesor(profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Alertas obtenidas exitosamente")
        );
    }

    /**
     * Obtener alertas de un curso
     * GET /alertas-temprana/curso/{cursoId}
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<ApiResponse<List<AlertaTempranaResponse>>> obtenerAlertasPorCurso(
            @PathVariable UUID cursoId,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("GET /alertas-temprana/curso/{} - Obtener alertas", cursoId);

        List<AlertaTempranaResponse> response = alertaService.obtenerAlertasPorCurso(cursoId, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Alertas obtenidas exitosamente")
        );
    }

    /**
     * Obtener una alerta por ID
     * GET /alertas-temprana/{alertaId}
     */
    @GetMapping("/{alertaId}")
    public ResponseEntity<ApiResponse<AlertaTempranaResponse>> obtenerAlertaPorId(
            @PathVariable UUID alertaId,
            @RequestHeader(value = "X-Profesor-Id", required = false) UUID profesorId,
            @RequestHeader(value = "X-Estudiante-Id", required = false) UUID estudianteId) {

        log.info("GET /alertas-temprana/{} - Obtener alerta", alertaId);

        UUID usuarioId = profesorId != null ? profesorId : estudianteId;
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Se requiere X-Profesor-Id o X-Estudiante-Id"));
        }

        AlertaTempranaResponse response = alertaService.obtenerAlertaPorId(alertaId, usuarioId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Alerta obtenida exitosamente")
        );
    }

    /**
     * Eliminar una alerta
     * DELETE /alertas-temprana/{alertaId}
     */
    @DeleteMapping("/{alertaId}")
    public ResponseEntity<ApiResponse<Void>> eliminarAlerta(
            @PathVariable UUID alertaId,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("DELETE /alertas-temprana/{} - Eliminar alerta", alertaId);

        alertaService.eliminarAlerta(alertaId, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Alerta eliminada exitosamente")
        );
    }
}


