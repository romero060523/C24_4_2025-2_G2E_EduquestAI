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

    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.mision.id = :misionId AND p.completada = true")
    Long countCompletadosByMision(@Param("misionId") UUID misionId);

    @Query("SELECT COUNT(p) FROM ProgresoMision p WHERE p.mision.id = :misionId " +
            "AND p.completada = false AND p.porcentajeCompletado > 0")
    Long countEnProgresoByMision(@Param("misionId") UUID misionId);
}
