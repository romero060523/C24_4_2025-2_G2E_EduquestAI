package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.GenerarActividadesAdaptadasRequest;
import com.eduquestia.backend.dto.response.ActividadesAdaptadasResponse;

public interface ActividadesAdaptadasAIService {
    ActividadesAdaptadasResponse generarActividadesAdaptadas(GenerarActividadesAdaptadasRequest request, java.util.UUID profesorId);
}

