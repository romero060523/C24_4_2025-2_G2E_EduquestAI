package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingResponse;

import java.util.UUID;

public interface GamificacionService {
    
    /**
     * Obtiene el perfil gamificado completo del estudiante
     */
    PerfilGamificadoResponse obtenerPerfilGamificado(UUID estudianteId);
    
    /**
     * Verifica y otorga logros al estudiante cuando gana puntos
     */
    void verificarYOtorgarLogros(UUID estudianteId);
    
    /**
     * Obtiene el ranking de estudiantes en un curso
     */
    RankingResponse obtenerRankingPorCurso(UUID cursoId);
    
    /**
     * Obtiene el ranking global de todos los estudiantes
     */
    RankingResponse obtenerRankingGlobal();
    
    /**
     * Calcula el nivel basado en puntos
     */
    Integer calcularNivel(Integer puntos);
    
    /**
     * Obtiene el nombre del nivel
     */
    String obtenerNombreNivel(Integer nivel);
    
    /**
     * Calcula puntos necesarios para el siguiente nivel
     */
    Integer calcularPuntosParaSiguienteNivel(Integer puntos);
}

