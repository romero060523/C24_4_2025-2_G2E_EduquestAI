package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.EstadoInscripcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad Inscripcion - SOLO LECTURA
 * Esta tabla es gestionada por el admin-backend (Django)
 * El client-backend solo consulta esta información como referencia
 */
@Entity
@Table(name = "inscripciones", schema = "grupo_03",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "curso_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    // Django usa OffsetDateTime (timestamp with time zone)
    @Column(name = "fecha_inscripcion", nullable = false)
    private OffsetDateTime fechaInscripcion;

    // Django guarda el estado como String, valores: 'activo', 'completado', 'retirado'
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_completado")
    private OffsetDateTime fechaCompletado;

    // Django usa OffsetDateTime (timestamp with time zone)
    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    // Método helper para obtener el estado como enum
    @Transient
    public EstadoInscripcion getEstadoEnum() {
        if (estado == null) return null;
        try {
            return EstadoInscripcion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Método helper para setear el estado desde enum
    public void setEstadoEnum(EstadoInscripcion estadoEnum) {
        this.estado = estadoEnum != null ? estadoEnum.name().toLowerCase() : null;
    }
}
