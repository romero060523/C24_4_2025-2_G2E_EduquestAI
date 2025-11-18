package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
import com.eduquestia.backend.entity.enums.TipoMision;
import com.eduquestia.backend.entity.enums.TemaVisual;
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
@Table(name = "misiones", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "curso_id", 
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_mision_curso")
    )
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "profesor_id", 
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_mision_profesor"),
        referencedColumnName = "id"
    )
    private Usuario profesor;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // ENUM: 'individual', 'grupal'
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mision", nullable = false, length = 20)
    private TipoMision tipoMision;

    // ENUM: 'lectura', 'ejercicio', 'proyecto', 'quiz', 'desafio'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaMision categoria;

    // ENUM: 'facil', 'medio', 'dificil', 'experto'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DificultadMision dificultad;

    @Column(name = "puntos_recompensa")
    private Integer puntosRecompensa = 0;

    @Column(name = "experiencia_recompensa")
    private Integer experienciaRecompensa = 0;

    @Column(name = "monedas_recompensa")
    private Integer monedasRecompensa = 0;

    @Column(name = "semana_clase")
    private Integer semanaClase;

    // ENUM: tema visual de la misión (medieval, anime, espacial, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "tema_visual", length = 20)
    private TemaVisual temaVisual = TemaVisual.DEFAULT;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "requisitos_previos", columnDefinition = "TEXT")
    private String requisitosPrevios;

    @OneToMany(mappedBy = "mision", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoMision> contenidos = new ArrayList<>();

    @OneToMany(mappedBy = "mision", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CriteriosEvaluacion> criterios = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos helper para gestionar relaciones bidireccionales
    public void addContenido(ContenidoMision contenido) {
        contenidos.add(contenido);
        contenido.setMision(this);
    }

    public void removeContenido(ContenidoMision contenido) {
        contenidos.remove(contenido);
        contenido.setMision(null);
    }

    public void addCriterio(CriteriosEvaluacion criterio) {
        criterios.add(criterio);
        criterio.setMision(this);
    }

    public void removeCriterio(CriteriosEvaluacion criterio) {
        criterios.remove(criterio);
        criterio.setMision(null);
    }
}

