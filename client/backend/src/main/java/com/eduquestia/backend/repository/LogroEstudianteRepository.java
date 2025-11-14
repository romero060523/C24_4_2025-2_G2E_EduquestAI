package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.LogroEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogroEstudianteRepository extends JpaRepository<LogroEstudiante, UUID> {
    List<LogroEstudiante> findByEstudianteId(UUID estudianteId);
    
    Optional<LogroEstudiante> findByEstudianteIdAndLogroId(UUID estudianteId, UUID logroId);
    
    @Query("SELECT COUNT(le) FROM LogroEstudiante le WHERE le.estudiante.id = :estudianteId")
    Long countLogrosByEstudianteId(@Param("estudianteId") UUID estudianteId);
}

