package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.ContenidoMision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContenidoMisionRepository extends JpaRepository<ContenidoMision, UUID> {
    List<ContenidoMision> findByMisionIdOrderByOrdenAsc(UUID misionId);
    void deleteByMisionId(UUID misionId);
}
