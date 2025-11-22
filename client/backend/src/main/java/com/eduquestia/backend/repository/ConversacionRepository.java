package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, UUID> {
    
    List<Conversacion> findByUsuarioIdOrderByFechaActualizacionDesc(UUID usuarioId);
    
    Optional<Conversacion> findByUsuarioIdAndId(UUID usuarioId, UUID id);
}
