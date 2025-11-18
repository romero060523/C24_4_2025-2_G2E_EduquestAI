package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.RecompensaManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecompensaManualRepository extends JpaRepository<RecompensaManual, UUID> {

    /**
     * Obtiene todas las recompensas manuales de un estudiante
     */
    List<RecompensaManual> findByEstudianteIdOrderByFechaOtorgamientoDesc(UUID estudianteId);

    /**
     * Obtiene todas las recompensas manuales otorgadas por un profesor
     */
    List<RecompensaManual> findByProfesorIdOrderByFechaOtorgamientoDesc(UUID profesorId);

    /**
     * Obtiene todas las recompensas manuales de un curso
     */
    List<RecompensaManual> findByCursoIdOrderByFechaOtorgamientoDesc(UUID cursoId);

    /**
     * Suma los puntos totales de recompensas manuales de un estudiante
     */
    @Query("SELECT COALESCE(SUM(r.puntos), 0) FROM RecompensaManual r WHERE r.estudiante.id = :estudianteId")
    Integer sumPuntosByEstudianteId(@Param("estudianteId") UUID estudianteId);

    /**
     * Cuenta las recompensas manuales de un estudiante
     */
    Long countByEstudianteId(UUID estudianteId);
}

