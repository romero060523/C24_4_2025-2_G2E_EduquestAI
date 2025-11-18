package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.CompletarMisionRequest;
import com.eduquestia.backend.dto.request.MisionCreateRequest;
import com.eduquestia.backend.dto.request.MisionUpdateRequest;
import com.eduquestia.backend.dto.response.MisionEstudianteResponse;
import com.eduquestia.backend.dto.response.MisionListResponse;
import com.eduquestia.backend.dto.response.MisionProgresoResponse;
import com.eduquestia.backend.dto.response.MisionResponse;
import com.eduquestia.backend.entity.enums.CategoriaMision;

import java.util.List;
import java.util.UUID;

public interface MisionService {

    MisionResponse crearMision(MisionCreateRequest request, UUID profesorId);

    MisionResponse obtenerMisionPorId(UUID misionId);

    List<MisionListResponse> listarMisionesPorProfesor(UUID profesorId);

    List<MisionListResponse> listarMisionesPorCurso(UUID cursoId);

    List<MisionListResponse> listarMisionesPorCursoYCategoria(UUID cursoId, CategoriaMision categoria);

    MisionResponse actualizarMision(UUID misionId, MisionUpdateRequest request, UUID profesorId);

    void eliminarMision(UUID misionId, UUID profesorId);

    MisionProgresoResponse obtenerProgresoMision(UUID misionId, UUID profesorId);

    void asignarMisionAEstudiantes(UUID misionId, List<UUID> estudiantesIds, UUID profesorId);

    void reasignarMisionATodosEstudiantes(UUID misionId, UUID profesorId);

    // Métodos para estudiantes
    List<MisionEstudianteResponse> listarMisionesPorEstudiante(UUID estudianteId);

    MisionEstudianteResponse completarMision(UUID misionId, CompletarMisionRequest request, UUID estudianteId);
    
    Integer obtenerPuntosTotalesEstudiante(UUID estudianteId);
}

