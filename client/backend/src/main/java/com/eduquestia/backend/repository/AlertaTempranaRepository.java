package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.AlertaTemprana;
import com.eduquestia.backend.entity.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertaTempranaRepository extends JpaRepository<AlertaTemprana, UUID> {

    // Obtener alertas de un estudiante
    List<AlertaTemprana> findByEstudianteIdOrderByFechaCreacionDesc(UUID estudianteId);

    // Obtener alertas activas de un estudiante
    List<AlertaTemprana> findByEstudianteIdAndEstadoOrderByFechaCreacionDesc(
            UUID estudianteId, EstadoAlerta estado);

    // Obtener alertas creadas por un profesor
    List<AlertaTemprana> findByProfesorIdOrderByFechaCreacionDesc(UUID profesorId);

    // Obtener alertas de un curso
    List<AlertaTemprana> findByCursoIdOrderByFechaCreacionDesc(UUID cursoId);

    // Obtener alertas activas de un curso
    @Query("SELECT a FROM AlertaTemprana a WHERE a.curso.id = :cursoId AND a.estado = :estado ORDER BY a.fechaCreacion DESC")
    List<AlertaTemprana> findActivasByCursoId(@Param("cursoId") UUID cursoId, @Param("estado") EstadoAlerta estado);

    // Contar alertas activas de un estudiante
    long countByEstudianteIdAndEstado(UUID estudianteId, EstadoAlerta estado);
}


