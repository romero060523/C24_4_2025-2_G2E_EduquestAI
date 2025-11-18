package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.AlertaRendimiento;
import com.eduquestia.backend.entity.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertaRendimientoRepository extends JpaRepository<AlertaRendimiento, UUID> {
    List<AlertaRendimiento> findByCursoIdAndEstado(UUID cursoId, EstadoAlerta estado);
    List<AlertaRendimiento> findByEstudianteIdAndEstado(UUID estudianteId, EstadoAlerta estado);

    @Query("SELECT a FROM AlertaRendimiento a WHERE a.curso.id = :cursoId AND a.estado = 'ACTIVA' ORDER BY a.fechaCreacion DESC")
    List<AlertaRendimiento> findAlertasActivasPorCurso(UUID cursoId);
}
