package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad CursoProfesor - SOLO LECTURA
 * Relación N:M entre cursos y profesores
 * Esta tabla es gestionada por el admin-backend (Django)
 */
@Entity
@Table(name = "cursos_profesores", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoProfesor {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Usuario profesor;

    @Column(name = "rol_profesor", length = 20)
    private String rolProfesor; // 'titular' o 'asistente'

    @Column(name = "fecha_asignacion")
    private OffsetDateTime fechaAsignacion;
}

