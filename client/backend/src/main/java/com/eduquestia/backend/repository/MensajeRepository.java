package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, UUID> {
    
    List<Mensaje> findByConversacionIdOrderByFechaCreacionAsc(UUID conversacionId);
}
