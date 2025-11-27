package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.CursoProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CursoProfesorRepository extends JpaRepository<CursoProfesor, UUID> {

    List<CursoProfesor> findByCursoId(UUID cursoId);

    List<CursoProfesor> findByProfesorId(UUID profesorId);

    Optional<CursoProfesor> findByCursoIdAndRolProfesor(UUID cursoId, String rolProfesor);

    boolean existsByCursoIdAndProfesorId(UUID cursoId, UUID profesorId);
}

