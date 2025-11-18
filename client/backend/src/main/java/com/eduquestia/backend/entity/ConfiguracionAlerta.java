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
@Table(name = "configuracion_alertas", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Usuario profesor;

    // Criterios de alerta
    @Column(name = "dias_inactividad")
    private Integer diasInactividad;

    @Column(name = "porcentaje_completitud_minimo")
    private Double porcentajeCompletitudMinimo;    // ej: < 50%

    @Column(name = "puntos_debajo_promedio")
    private Boolean puntosDebajoPromedio; // true/false

    @Column(name = "misiones_pendientes_minimo")
    private Integer misionesPendientesMinimo; // ej: >= 3 misiones sin completar

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable= false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fecha_actualizacion;
}
