package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.TipoPregunta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "preguntas", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private EvaluacionGamificada evaluacion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pregunta", nullable = false, length = 30)
    private TipoPregunta tipoPregunta;

    @Column(name = "puntos", nullable = false)
    private Integer puntos = 10;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl; // Para preguntas con imágenes

    @Column(name = "explicacion", columnDefinition = "TEXT")
    private String explicacion; // Explicación de la respuesta correcta

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<OpcionRespuesta> opciones = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public void addOpcion(OpcionRespuesta opcion) {
        opciones.add(opcion);
        opcion.setPregunta(this);
    }

    public void removeOpcion(OpcionRespuesta opcion) {
        opciones.remove(opcion);
        opcion.setPregunta(null);
    }
}


