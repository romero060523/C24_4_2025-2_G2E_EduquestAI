package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.CrearEvaluacionRequest;
import com.eduquestia.backend.dto.request.ResponderEvaluacionRequest;
import com.eduquestia.backend.dto.response.EvaluacionGamificadaResponse;
import com.eduquestia.backend.dto.response.ResultadoEvaluacionResponse;

import java.util.List;
import java.util.UUID;

public interface EvaluacionGamificadaService {
    EvaluacionGamificadaResponse crearEvaluacion(CrearEvaluacionRequest request, UUID profesorId);
    EvaluacionGamificadaResponse obtenerEvaluacionPorMision(UUID misionId, UUID estudianteId);
    EvaluacionGamificadaResponse obtenerEvaluacionPorId(UUID evaluacionId, UUID estudianteId);
    ResultadoEvaluacionResponse responderEvaluacion(ResponderEvaluacionRequest request, UUID estudianteId);
    List<ResultadoEvaluacionResponse> obtenerResultadosPorEstudiante(UUID evaluacionId, UUID estudianteId);
    List<ResultadoEvaluacionResponse> obtenerResultadosPorEvaluacion(UUID evaluacionId, UUID profesorId);
    void eliminarEvaluacion(UUID evaluacionId, UUID profesorId);
    Integer obtenerIntentosRestantes(UUID evaluacionId, UUID estudianteId);
    List<EvaluacionGamificadaResponse> listarEvaluacionesPorCurso(UUID cursoId);
    List<EvaluacionGamificadaResponse> listarEvaluacionesProfesor(UUID profesorId);
    List<EvaluacionGamificadaResponse> listarEvaluacionesEstudiante(UUID estudianteId);
}


