package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.CriteriosEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CriteriosEvaluacionRepository extends JpaRepository<CriteriosEvaluacion, UUID> {
    List<CriteriosEvaluacion> findByMisionIdOrderByOrdenAsc(UUID misionId);
    void deleteByMisionId(UUID misionId);
}
