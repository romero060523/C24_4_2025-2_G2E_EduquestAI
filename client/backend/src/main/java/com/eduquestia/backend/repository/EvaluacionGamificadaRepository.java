package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.EvaluacionGamificada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluacionGamificadaRepository extends JpaRepository<EvaluacionGamificada, UUID> {
    
    List<EvaluacionGamificada> findByMisionIdAndActivoTrue(UUID misionId);
    
    @Query("SELECT DISTINCT e FROM EvaluacionGamificada e LEFT JOIN FETCH e.preguntas p WHERE e.id = :id")
    Optional<EvaluacionGamificada> findByIdWithPreguntas(@Param("id") UUID id);
    
    @Query("SELECT e FROM EvaluacionGamificada e WHERE e.mision.id = :misionId AND e.activo = true")
    List<EvaluacionGamificada> findActivasByMisionId(@Param("misionId") UUID misionId);
    
    // Listar evaluaciones por curso
    @Query("SELECT e FROM EvaluacionGamificada e WHERE e.curso.id = :cursoId ORDER BY e.fechaCreacion DESC")
    List<EvaluacionGamificada> findByCursoId(@Param("cursoId") UUID cursoId);
    
    // Listar evaluaciones por profesor (a través de los cursos)
    @Query("SELECT e FROM EvaluacionGamificada e WHERE e.curso.id IN (SELECT c.id FROM Curso c WHERE c.id IN :cursosIds) ORDER BY e.fechaCreacion DESC")
    List<EvaluacionGamificada> findByProfesorCursos(@Param("cursosIds") List<UUID> cursosIds);
}


