package com.eduquestia.backend.repository;

import com.eduquestia.backend.entity.Usuario;
import com.eduquestia.backend.entity.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);
}
