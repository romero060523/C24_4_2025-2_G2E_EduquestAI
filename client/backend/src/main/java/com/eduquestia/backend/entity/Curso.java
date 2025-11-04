package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad Curso - SOLO LECTURA
 * Esta tabla es gestionada por el admin-backend (Django)
 * El client-backend solo consulta esta información como referencia
 */
@Entity
@Table(name = "cursos", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "codigo_curso", unique = true, nullable = false, length = 20)
    private String codigoCurso;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "imagen_portada", length = 255)
    private String imagenPortada;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activo = true;

    // Django usa OffsetDateTime (timestamp with time zone)
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
