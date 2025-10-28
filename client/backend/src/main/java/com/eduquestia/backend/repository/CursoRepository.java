package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID> {
    Optional<Curso> findByCodigoCurso(String codigoCurso);
    List<Curso> findByActivoTrue();
}
