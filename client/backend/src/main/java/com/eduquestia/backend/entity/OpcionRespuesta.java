package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "opciones_respuesta", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "es_correcta", nullable = false)
    private Boolean esCorrecta = false;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl; // Para opciones con imágenes

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback; // Feedback específico para esta opción
}


