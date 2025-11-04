package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogroRepository extends JpaRepository<Logro, UUID> {
    List<Logro> findByActivoTrueOrderByPuntosRequeridosAsc();
}

