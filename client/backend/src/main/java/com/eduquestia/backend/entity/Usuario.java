package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    // Django usa "password", no "password_hash" - VARCHAR(255) según diseño DB
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Django guarda el rol como String, no como ENUM en la BD
    // Valores: 'administrador', 'profesor', 'estudiante'
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Django usa OffsetDateTime (timestamp with time zone)
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @Column(name = "ultimo_acceso")
    private OffsetDateTime ultimoAcceso;

    // Campos adicionales de Django que existen en la tabla
    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperuser = false;

    @Column(name = "is_staff", nullable = false)
    private Boolean isStaff = false;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    // Método helper para obtener el rol como enum
    @Transient
    public RolUsuario getRolEnum() {
        if (rol == null) return null;
        try {
            return RolUsuario.valueOf(rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Método helper para setear el rol desde enum
    public void setRolEnum(RolUsuario rolEnum) {
        this.rol = rolEnum != null ? rolEnum.name().toLowerCase() : null;
    }
}
