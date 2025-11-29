package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.OtorgarRecompensaRequest;
import com.eduquestia.backend.dto.response.PerfilGamificadoResponse;
import com.eduquestia.backend.dto.response.RankingResponse;
import com.eduquestia.backend.dto.response.RecompensaManualResponse;

import java.util.List;
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
    
    /**
     * Otorga una recompensa manual a un estudiante
     * Historia de Usuario #12: Recompensas manuales
     */
    RecompensaManualResponse otorgarRecompensaManual(OtorgarRecompensaRequest request, UUID profesorId);
    
    /**
     * Obtiene todas las recompensas manuales de un estudiante
     */
    List<RecompensaManualResponse> obtenerRecompensasManualesPorEstudiante(UUID estudianteId);
    
    /**
     * Obtiene todas las recompensas manuales otorgadas por un profesor
     */
    List<RecompensaManualResponse> obtenerRecompensasManualesPorProfesor(UUID profesorId);
    
    /**
     * Obtiene el total de puntos de recompensas manuales de un estudiante
     */
    Integer obtenerPuntosRecompensasManuales(UUID estudianteId);
    
    /**
     * Analiza el progreso del estudiante y genera sugerencias de metas y recompensas usando IA
     * Historia de Usuario #20: IA analiza progreso y sugiere metas
     */
    com.eduquestia.backend.dto.response.SugerenciaIAResponse generarSugerenciasIA(UUID estudianteId);
}

