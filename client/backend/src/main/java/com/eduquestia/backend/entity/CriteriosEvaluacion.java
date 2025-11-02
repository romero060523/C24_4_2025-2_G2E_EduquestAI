package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "criterios_evaluacion", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriteriosEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", nullable = false)
    private Mision mision;

    @Column(nullable = false, length = 200)
    private String criterio;

    @Column(name = "puntos_maximos", nullable = false)
    private Integer puntosMaximos;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer orden = 0;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
