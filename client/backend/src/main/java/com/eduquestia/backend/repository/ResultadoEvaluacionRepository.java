package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.ResultadoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultadoEvaluacionRepository extends JpaRepository<ResultadoEvaluacion, UUID> {
    
    List<ResultadoEvaluacion> findByEvaluacionIdAndEstudianteId(UUID evaluacionId, UUID estudianteId);
    
    Optional<ResultadoEvaluacion> findByEvaluacionIdAndEstudianteIdAndIntentoNumero(
            UUID evaluacionId, UUID estudianteId, Integer intentoNumero);
    
    @Query("SELECT MAX(r.intentoNumero) FROM ResultadoEvaluacion r WHERE r.evaluacion.id = :evaluacionId AND r.estudiante.id = :estudianteId")
    Integer findMaxIntentoNumero(@Param("evaluacionId") UUID evaluacionId, @Param("estudianteId") UUID estudianteId);
    
    List<ResultadoEvaluacion> findByEvaluacionId(UUID evaluacionId);
}


