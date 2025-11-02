package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "progreso_mision", schema = "grupo_03",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mision_id", "estudiante_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoMision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", nullable = false)
    private Mision mision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @Column(name = "porcentaje_completado", nullable = false)
    private Integer porcentajeCompletado = 0;

    @Column(name = "tiempo_dedicado_minutos", nullable = false)
    private Integer tiempoDedicadoMinutos = 0;

    @Column(name = "ultima_actividad", nullable = false)
    private LocalDateTime ultimaActividad = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean completada = false;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
