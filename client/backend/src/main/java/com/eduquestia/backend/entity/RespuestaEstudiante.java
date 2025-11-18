package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "respuestas_estudiante", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionGamificada evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcion_id")
    private OpcionRespuesta opcionSeleccionada; // Para opción múltiple

    @Column(name = "respuesta_texto", columnDefinition = "TEXT")
    private String respuestaTexto; // Para completar espacios, ordenar, etc.

    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta = false;

    @Column(name = "puntos_obtenidos", nullable = false)
    private Integer puntosObtenidos = 0;

    @Column(name = "tiempo_respuesta_segundos")
    private Integer tiempoRespuestaSegundos; // Tiempo que tardó en responder

    @Column(name = "intento_numero", nullable = false)
    private Integer intentoNumero = 1;

    @CreationTimestamp
    @Column(name = "fecha_respuesta", nullable = false, updatable = false)
    private LocalDateTime fechaRespuesta;
}


