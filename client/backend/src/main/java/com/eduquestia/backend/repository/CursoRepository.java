package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Curso - SOLO LECTURA
 * Esta tabla es gestionada por el admin-backend (Django)
 * El client-backend solo realiza consultas de lectura
 * NO usar métodos save(), delete(), etc.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, UUID> {
    
    // Consultas de lectura permitidas
    Optional<Curso> findByCodigoCurso(String codigoCurso);
    List<Curso> findByActivoTrue();
    List<Curso> findByActivoOrderByFechaCreacionDesc(Boolean activo);
}
