package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.ProgresoMision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgresoMisionRepository extends JpaRepository<ProgresoMision, UUID> {

    Optional<ProgresoMision> findByMisionIdAndEstudianteId(UUID misionId, UUID estudianteId);

    List<ProgresoMision> findByMisionId(UUID misionId);

    List<ProgresoMision> findByEstudianteId(UUID estudianteId);

    @Query("SELECT p FROM ProgresoMision p " +
           "JOIN FETCH p.mision m " +
           "JOIN FETCH m.curso c " +
           "WHERE p.estudiante.id = :estudianteId")
    List<ProgresoMision> findByEstudianteIdWithMisionAndCurso(@Param("estudianteId") UUID estudianteId);

    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.mision.id = :misionId AND p.completada = true")
    Long countCompletadosByMision(@Param("misionId") UUID misionId);

    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.mision.id = :misionId " +
            "AND p.completada = false AND p.porcentajeCompletado > 0")
    Long countEnProgresoByMision(@Param("misionId") UUID misionId);

    Optional<ProgresoMision> findTopByEstudianteIdOrderByFechaActualizacionDesc(UUID estudianteId);

    // Nuevos métodos para alertas
    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.estudiante.id = :estudianteId AND p.mision.curso.id = :cursoId")
    long countByEstudianteIdAndCursoId(@Param("estudianteId") UUID estudianteId, @Param("cursoId") UUID cursoId);

    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.estudiante.id = :estudianteId AND p.mision.curso.id = :cursoId AND p.completada = :completado")
    long countByEstudianteIdAndCursoIdAndCompletado(
            @Param("estudianteId") UUID estudianteId,
            @Param("cursoId") UUID cursoId,
            @Param("completado") boolean completado);

    @Query("SELECT COALESCE(SUM(p.mision.puntosRecompensa), 0) FROM ProgresoMision p WHERE p.estudiante.id = :estudianteId AND p.mision.curso.id = :cursoId AND p.completada = true")
    Optional<Integer> sumPuntosByEstudianteIdAndCursoId(
            @Param("estudianteId") UUID estudianteId,
            @Param("cursoId") UUID cursoId);

    @Query(value = "SELECT COALESCE(AVG(total_puntos), 0.0) FROM (SELECT SUM(m.puntos_recompensa) as total_puntos FROM grupo_03.progreso_mision p INNER JOIN grupo_03.misiones m ON p.mision_id = m.id WHERE m.curso_id = :cursoId AND p.completada = true GROUP BY p.estudiante_id) as subquery", nativeQuery = true)
    Optional<Double> avgPuntosByCursoId(@Param("cursoId") UUID cursoId);
}
