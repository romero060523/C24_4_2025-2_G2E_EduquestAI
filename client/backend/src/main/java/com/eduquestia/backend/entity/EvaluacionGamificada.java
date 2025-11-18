package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evaluaciones_gamificada", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionGamificada {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", nullable = true)
    private Mision mision; // OPCIONAL: solo si está asociada a una misión específica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso; // REQUERIDO: toda evaluación pertenece a un curso

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_limite_minutos")
    private Integer tiempoLimiteMinutos; // null = sin límite

    @Column(name = "intentos_permitidos")
    private Integer intentosPermitidos = 1;

    @Column(name = "mostrar_resultados_inmediato", nullable = false)
    private Boolean mostrarResultadosInmediato = true;

    @Column(name = "puntos_por_pregunta", nullable = false)
    private Integer puntosPorPregunta = 10;

    @Column(name = "puntos_bonus_tiempo")
    private Integer puntosBonusTiempo = 5; // Puntos extra por completar rápido

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<Pregunta> preguntas = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public void addPregunta(Pregunta pregunta) {
        preguntas.add(pregunta);
        pregunta.setEvaluacion(this);
    }

    public void removePregunta(Pregunta pregunta) {
        preguntas.remove(pregunta);
        pregunta.setEvaluacion(null);
    }
}


