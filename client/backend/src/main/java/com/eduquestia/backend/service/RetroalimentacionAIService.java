package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.GenerarRetroalimentacionRequest;
import com.eduquestia.backend.dto.response.RetroalimentacionResponse;

public interface RetroalimentacionAIService {
    RetroalimentacionResponse generarRetroalimentacion(GenerarRetroalimentacionRequest request, java.util.UUID profesorId);
}

