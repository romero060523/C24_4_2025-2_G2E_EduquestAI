package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.EstadoEntrega;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entregas_mision", schema = "grupo_03",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mision_id", "estudiante_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregaMision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", nullable = false)
    private Mision mision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    // ENUM: 'pendiente', 'enviada', 'revisando', 'calificada', 'rechazada'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEntrega estado = EstadoEntrega.PENDIENTE;

    @Column(name = "contenido_entrega", columnDefinition = "TEXT")
    private String contenidoEntrega;

    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion;

    @Column(precision = 5, scale = 2)
    private BigDecimal calificacion;

    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos;

    @Column(name = "comentarios_profesor", columnDefinition = "TEXT")
    private String comentariosProfesor;

    @Column(name = "comentarios_estudiante", columnDefinition = "TEXT")
    private String comentariosEstudiante;

    @Column(nullable = false)
    private Integer intentos = 1;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
