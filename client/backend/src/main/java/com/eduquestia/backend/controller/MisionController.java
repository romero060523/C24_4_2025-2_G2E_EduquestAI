package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.MisionCreateRequest;
import com.eduquestia.backend.dto.request.MisionUpdateRequest;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.dto.response.MisionListResponse;
import com.eduquestia.backend.dto.response.MisionProgresoResponse;
import com.eduquestia.backend.dto.response.MisionResponse;
import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.service.MisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/misiones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:19006"})
public class MisionController {

    private final MisionService misionService;

    /**
     * Crear una nueva misión
     * POST /api/v1/missions
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MisionResponse>> crearMision(
            @Valid @RequestBody MisionCreateRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /misiones - Crear misión: {}", request.getTitulo());

        MisionResponse response = misionService.crearMision(request, profesorId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Misión creada exitosamente"));
    }

    /**
     * Obtener detalle de una misión
     * GET /api/v1/missions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MisionResponse>> obtenerMision(
            @PathVariable UUID id) {

        log.info("GET /misiones/{} - Obtener misión", id);

        MisionResponse response = misionService.obtenerMisionPorId(id);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Misión obtenida exitosamente")
        );
    }

    /**
     * Listar todas las misiones del profesor
     * GET /api/v1/missions/profesor/{profesorId}
     */
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<ApiResponse<List<MisionListResponse>>> listarMisionesPorProfesor(
            @PathVariable UUID profesorId) {

        log.info("GET /misiones/profesor/{} - Listar misiones", profesorId);

        List<MisionListResponse> response = misionService.listarMisionesPorProfesor(profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Misiones obtenidas exitosamente")
        );
    }

    /**
     * Listar misiones de un curso
     * GET /api/v1/missions/curso/{cursoId}
     * Query param opcional: categoria
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<ApiResponse<List<MisionListResponse>>> listarMisionesPorCurso(
            @PathVariable UUID cursoId,
            @RequestParam(required = false) CategoriaMision categoria) {

        log.info("GET /misiones/curso/{} - Listar misiones", cursoId);

        List<MisionListResponse> response;

        if (categoria != null) {
            response = misionService.listarMisionesPorCursoYCategoria(cursoId, categoria);
        } else {
            response = misionService.listarMisionesPorCurso(cursoId);
        }

        return ResponseEntity.ok(
                ApiResponse.success(response, "Misiones obtenidas exitosamente")
        );
    }

    /**
     * Actualizar una misión
     * PUT /api/v1/missions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MisionResponse>> actualizarMision(
            @PathVariable UUID id,
            @Valid @RequestBody MisionUpdateRequest request,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("PUT /misiones/{} - Actualizar misión", id);

        MisionResponse response = misionService.actualizarMision(id, request, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Misión actualizada exitosamente")
        );
    }

    /**
     * Eliminar una misión
     * DELETE /api/v1/missions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMision(
            @PathVariable UUID id,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("DELETE /misiones/{} - Eliminar misión", id);

        misionService.eliminarMision(id, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Misión eliminada exitosamente")
        );
    }

    /**
     * Obtener progreso de una misión
     * GET /api/v1/missions/{id}/progreso
     */
    @GetMapping("/{id}/progreso")
    public ResponseEntity<ApiResponse<MisionProgresoResponse>> obtenerProgresoMision(
            @PathVariable UUID id,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("GET /misiones/{}/progreso - Obtener progreso", id);

        MisionProgresoResponse response = misionService.obtenerProgresoMision(id, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Progreso obtenido exitosamente")
        );
    }

    /**
     * Asignar misión a estudiantes específicos
     * POST /api/v1/missions/{id}/asignar
     */
    @PostMapping("/{id}/asignar")
    public ResponseEntity<ApiResponse<Void>> asignarMisionAEstudiantes(
            @PathVariable UUID id,
            @RequestBody List<UUID> estudiantesIds,
            @RequestHeader("X-Profesor-Id") UUID profesorId) {

        log.info("POST /misiones/{}/asignar - Asignar a {} estudiantes", id, estudiantesIds.size());

        misionService.asignarMisionAEstudiantes(id, estudiantesIds, profesorId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Misión asignada exitosamente")
        );
    }
}
