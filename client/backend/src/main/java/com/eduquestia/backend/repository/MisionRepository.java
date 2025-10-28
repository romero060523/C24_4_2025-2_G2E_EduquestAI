package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Mision;
import com.eduquestia.backend.entity.enums.CategoriaMision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MisionRepository extends JpaRepository<Mision, UUID> {

    List<Mision> findByCursoId(UUID cursoId);

    List<Mision> findByProfesorId(UUID profesorId);

    List<Mision> findByCursoIdAndActivoTrue(UUID cursoId);

    List<Mision> findByProfesorIdAndActivoTrue(UUID profesorId);

    List<Mision> findByCursoIdAndCategoria(UUID cursoId, CategoriaMision categoria);

    @Query("SELECT m FROM Mision m WHERE m.profesor.id = :profesorId " +
            "AND m.activo = true " +
            "ORDER BY m.fechaLimite ASC")
    List<Mision> findMisionesByProfesor(@Param("profesorId") UUID profesorId);

    @Query("SELECT m FROM Mision m WHERE m.curso.id = :cursoId " +
            "AND m.fechaLimite BETWEEN :inicio AND :fin")
    List<Mision> findMisionesPorRangoFecha(
            @Param("cursoId") UUID cursoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("SELECT COUNT(m) FROM Mision m WHERE m.curso.id = :cursoId AND m.activo = true")
    Long countMisionesActivasByCurso(@Param("cursoId") UUID cursoId);
}

