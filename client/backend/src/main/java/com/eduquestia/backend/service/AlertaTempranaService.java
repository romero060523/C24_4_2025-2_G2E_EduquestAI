package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.ActualizarAlertaRequest;
import com.eduquestia.backend.dto.request.CrearAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaTempranaResponse;

import java.util.List;
import java.util.UUID;

public interface AlertaTempranaService {
    AlertaTempranaResponse crearAlerta(CrearAlertaRequest request, UUID profesorId);
    AlertaTempranaResponse actualizarAlerta(UUID alertaId, ActualizarAlertaRequest request, UUID profesorId);
    List<AlertaTempranaResponse> obtenerAlertasPorEstudiante(UUID estudianteId);
    List<AlertaTempranaResponse> obtenerAlertasPorProfesor(UUID profesorId);
    List<AlertaTempranaResponse> obtenerAlertasPorCurso(UUID cursoId, UUID profesorId);
    AlertaTempranaResponse obtenerAlertaPorId(UUID alertaId, UUID usuarioId);
    void eliminarAlerta(UUID alertaId, UUID profesorId);
}


