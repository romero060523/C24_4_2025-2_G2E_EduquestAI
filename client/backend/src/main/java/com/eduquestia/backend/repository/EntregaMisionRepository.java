package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.EntregaMision;
import com.eduquestia.backend.entity.enums.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntregaMisionRepository extends JpaRepository<EntregaMision, UUID> {

    Optional<EntregaMision> findByMisionIdAndEstudianteId(UUID misionId, UUID estudianteId);

    List<EntregaMision> findByMisionId(UUID misionId);

    List<EntregaMision> findByMisionIdAndEstado(UUID misionId, EstadoEntrega estado);

    List<EntregaMision> findByEstudianteId(UUID estudianteId);

    Long countByMisionId(UUID misionId);
}
