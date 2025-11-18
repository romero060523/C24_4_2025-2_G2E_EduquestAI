package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, UUID> {
    
    List<Pregunta> findByEvaluacionIdOrderByOrdenAsc(UUID evaluacionId);
    
    @Query("SELECT p FROM Pregunta p JOIN FETCH p.opciones WHERE p.evaluacion.id = :evaluacionId ORDER BY p.orden ASC")
    List<Pregunta> findByEvaluacionIdWithOpciones(@Param("evaluacionId") UUID evaluacionId);
}


