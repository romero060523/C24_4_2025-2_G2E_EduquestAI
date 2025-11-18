package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.CrearEvaluacionRequest;
import com.eduquestia.backend.dto.request.ResponderEvaluacionRequest;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.EvaluacionGamificadaResponse;
import com.eduquestia.backend.dto.response.ResultadoEvaluacionResponse;
import com.eduquestia.backend.service.EvaluacionGamificadaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evaluaciones-gamificadas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006", "http://localhost:3001"})
public class EvaluacionGamificadaController {

    private final EvaluacionGamificadaService evaluacionService;

    /**
     * Crear una nueva evaluación gamificada
     * POST /evaluaciones-gamificada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionGamificadaResponse>> crearEvaluacion(
            @Valid @RequestBody CrearEvaluacionRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /evaluaciones-gamificada - Crear evaluación para misión: {}", request.getMisionId());

        EvaluacionGamificadaResponse response = evaluacionService.crearEvaluacion(request, profesorId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Evaluación creada exitosamente"));
    }

    /**
     * Obtener evaluación por misión
     * GET /evaluaciones-gamificada/mision/{misionId}
     */
    @GetMapping("/mision/{misionId}")
    public ResponseEntity<ApiResponse<EvaluacionGamificadaResponse>> obtenerEvaluacionPorMision(
            @PathVariable UUID misionId,
            @RequestHeader("X-Estudiante-Id") UUID estudianteId) {

        log.info("GET /evaluaciones-gamificada/mision/{} - Obtener evaluación", misionId);

        EvaluacionGamificadaResponse response = evaluacionService.obtenerEvaluacionPorMision(misionId, estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluación obtenida exitosamente")
        );
    }

    /**
     * Obtener evaluación por ID
     * GET /evaluaciones-gamificada/{evaluacionId}
     */
    @GetMapping("/{evaluacionId}")
    public ResponseEntity<ApiResponse<EvaluacionGamificadaResponse>> obtenerEvaluacionPorId(
            @PathVariable UUID evaluacionId,
            @RequestHeader(value = "X-Profesor-Id", required = false) UUID profesorId,
            @RequestHeader(value = "X-Estudiante-Id", required = false) UUID estudianteId) {

        log.info("GET /evaluaciones-gamificada/{} - Obtener evaluación", evaluacionId);

        UUID usuarioId = profesorId != null ? profesorId : estudianteId;
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Se requiere X-Profesor-Id o X-Estudiante-Id"));
        }

        EvaluacionGamificadaResponse response = evaluacionService.obtenerEvaluacionPorId(evaluacionId, usuarioId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluación obtenida exitosamente")
        );
    }

    /**
     * Responder evaluación
     * POST /evaluaciones-gamificada/responder
     */
    @PostMapping("/responder")
    public ResponseEntity<ApiResponse<ResultadoEvaluacionResponse>> responderEvaluacion(
            @Valid @RequestBody ResponderEvaluacionRequest request,
            @RequestHeader("X-Estudiante-Id") UUID estudianteId) {

        log.info("POST /evaluaciones-gamificada/responder - Evaluación: {}", request.getEvaluacionId());

        ResultadoEvaluacionResponse response = evaluacionService.responderEvaluacion(request, estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluación completada exitosamente")
        );
    }

    /**
     * Obtener resultados de un estudiante
     * GET /evaluaciones-gamificada/{evaluacionId}/resultados/estudiante
     */
    @GetMapping("/{evaluacionId}/resultados/estudiante")
    public ResponseEntity<ApiResponse<List<ResultadoEvaluacionResponse>>> obtenerResultadosEstudiante(
            @PathVariable UUID evaluacionId,
            @RequestHeader("X-Estudiante-Id") UUID estudianteId) {

        log.info("GET /evaluaciones-gamificada/{}/resultados/estudiante", evaluacionId);

        List<ResultadoEvaluacionResponse> response = evaluacionService.obtenerResultadosPorEstudiante(evaluacionId, estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Resultados obtenidos exitosamente")
        );
    }

    /**
     * Obtener intentos restantes
     * GET /evaluaciones-gamificada/{evaluacionId}/intentos-restantes
     */
    @GetMapping("/{evaluacionId}/intentos-restantes")
    public ResponseEntity<ApiResponse<Integer>> obtenerIntentosRestantes(
            @PathVariable UUID evaluacionId,
            @RequestHeader("X-Estudiante-Id") UUID estudianteId) {

        log.info("GET /evaluaciones-gamificada/{}/intentos-restantes", evaluacionId);

        Integer intentos = evaluacionService.obtenerIntentosRestantes(evaluacionId, estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(intentos, "Intentos restantes obtenidos exitosamente")
        );
    }

    /**
     * Listar todas las evaluaciones de un curso
     * GET /evaluaciones-gamificadas/curso/{cursoId}
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<ApiResponse<List<EvaluacionGamificadaResponse>>> listarEvaluacionesPorCurso(
            @PathVariable UUID cursoId) {

        log.info("GET /evaluaciones-gamificadas/curso/{} - Listar evaluaciones", cursoId);

        List<EvaluacionGamificadaResponse> response = evaluacionService.listarEvaluacionesPorCurso(cursoId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluaciones obtenidas exitosamente")
        );
    }

    /**
     * Listar todas las evaluaciones de un profesor
     * GET /evaluaciones-gamificadas/profesor
     */
    @GetMapping("/profesor")
    public ResponseEntity<ApiResponse<List<EvaluacionGamificadaResponse>>> listarEvaluacionesProfesor(
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("GET /evaluaciones-gamificadas/profesor - Listar evaluaciones del profesor: {}", profesorId);

        List<EvaluacionGamificadaResponse> response = evaluacionService.listarEvaluacionesProfesor(profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluaciones obtenidas exitosamente")
        );
    }

    /**
     * Listar todas las evaluaciones disponibles para un estudiante
     * GET /evaluaciones-gamificadas/estudiante
     */
    @GetMapping("/estudiante")
    public ResponseEntity<ApiResponse<List<EvaluacionGamificadaResponse>>> listarEvaluacionesEstudiante(
            @RequestHeader("X-Estudiante-Id") UUID estudianteId) {

        log.info("GET /evaluaciones-gamificadas/estudiante - Listar evaluaciones para estudiante: {}", estudianteId);

        List<EvaluacionGamificadaResponse> response = evaluacionService.listarEvaluacionesEstudiante(estudianteId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Evaluaciones obtenidas exitosamente")
        );
    }

    /**
     * Eliminar evaluación
     * DELETE /evaluaciones-gamificada/{evaluacionId}
     */
    @DeleteMapping("/{evaluacionId}")
    public ResponseEntity<ApiResponse<Void>> eliminarEvaluacion(
            @PathVariable UUID evaluacionId,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("DELETE /evaluaciones-gamificada/{} - Eliminar evaluación", evaluacionId);

        evaluacionService.eliminarEvaluacion(evaluacionId, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Evaluación eliminada exitosamente")
        );
    }
}


