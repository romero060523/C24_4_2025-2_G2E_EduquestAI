package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resultados_evaluacion", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionGamificada evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @Column(name = "puntos_totales", nullable = false)
    private Integer puntosTotales = 0;

    @Column(name = "puntos_maximos", nullable = false)
    private Integer puntosMaximos = 0;

    @Column(name = "puntos_bonus", nullable = false)
    private Integer puntosBonus = 0; // Bonus por tiempo, racha, etc.

    @Column(name = "porcentaje", nullable = false)
    private Double porcentaje = 0.0;

    @Column(name = "preguntas_correctas", nullable = false)
    private Integer preguntasCorrectas = 0;

    @Column(name = "preguntas_totales", nullable = false)
    private Integer preguntasTotales = 0;

    @Column(name = "tiempo_total_segundos")
    private Integer tiempoTotalSegundos;

    @Column(name = "intento_numero", nullable = false)
    private Integer intentoNumero = 1;

    @Column(name = "completada", nullable = false)
    private Boolean completada = false;

    @CreationTimestamp
    @Column(name = "fecha_completado", nullable = false, updatable = false)
    private LocalDateTime fechaCompletado;
}


