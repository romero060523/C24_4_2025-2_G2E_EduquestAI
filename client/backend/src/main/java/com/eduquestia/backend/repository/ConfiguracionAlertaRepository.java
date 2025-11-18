package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.ConfiguracionAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracionAlertaRepository extends JpaRepository<ConfiguracionAlerta, UUID>{
    Optional<ConfiguracionAlerta> findByCursoIdAndActivoTrue(UUID cursoId);
    List<ConfiguracionAlerta> findByProfesorIdAndActivoTrue(UUID profesorId);
    List<ConfiguracionAlerta> findByActivoTrue();
}
