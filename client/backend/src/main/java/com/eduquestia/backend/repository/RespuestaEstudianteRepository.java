package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.RespuestaEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RespuestaEstudianteRepository extends JpaRepository<RespuestaEstudiante, UUID> {
    
    List<RespuestaEstudiante> findByEvaluacionIdAndEstudianteId(UUID evaluacionId, UUID estudianteId);
    
    List<RespuestaEstudiante> findByEvaluacionIdAndEstudianteIdAndIntentoNumero(
            UUID evaluacionId, UUID estudianteId, Integer intentoNumero);
    
    @Query("SELECT COUNT(r) FROM RespuestaEstudiante r WHERE r.evaluacion.id = :evaluacionId AND r.estudiante.id = :estudianteId")
    Long countByEvaluacionAndEstudiante(@Param("evaluacionId") UUID evaluacionId, @Param("estudianteId") UUID estudianteId);
}


