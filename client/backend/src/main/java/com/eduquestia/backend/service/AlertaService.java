package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.ConfiguracionAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaResponse;
import com.eduquestia.backend.dto.response.ConfiguracionAlertaResponse;

import java.util.List;
import java.util.UUID;

public interface AlertaService {
    UUID configurarAlertas(ConfiguracionAlertaRequest request, UUID profesorId);
    void evaluarEstudiantesCurso(UUID configuracionId);
    List<AlertaResponse> obtenerAlertasActivasPorCurso(UUID cursoId);
    List<AlertaResponse> obtenerAlertasActivasPorEstudiante(UUID estudianteId);
    void resolverAlerta(UUID alertaId);
    void ignorarAlerta(UUID alertaId);
    void evaluarCursoManualmente(UUID cursoId);
    ConfiguracionAlertaResponse obtenerConfiguracionPorCurso(UUID cursoId);
}
