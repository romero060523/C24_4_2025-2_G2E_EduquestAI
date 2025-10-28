package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, UUID> {

    List<Inscripcion> findByCursoId(UUID cursoId);

    List<Inscripcion> findByEstudianteId(UUID estudianteId);

    Optional<Inscripcion> findByEstudianteIdAndCursoId(UUID estudianteId, UUID cursoId);

    @Query("SELECT i.estudiante.id FROM Inscripcion i WHERE i.curso.id = :cursoId")
    List<UUID> findEstudiantesIdsByCursoId(@Param("cursoId") UUID cursoId);

    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.curso.id = :cursoId")
    Long countEstudiantesByCurso(@Param("cursoId") UUID cursoId);
}
