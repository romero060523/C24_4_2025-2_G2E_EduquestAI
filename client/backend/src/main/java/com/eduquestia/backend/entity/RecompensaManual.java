package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad para recompensas manuales otorgadas por profesores a estudiantes
 * Historia de Usuario #12: Recompensas manuales
 */
@Entity
@Table(name = "recompensas_manuales", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecompensaManual {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "profesor_id", 
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_recompensa_profesor")
    )
    private Usuario profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "estudiante_id", 
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_recompensa_estudiante")
    )
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "curso_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_recompensa_curso")
    )
    private Curso curso;

    @Column(nullable = false)
    private Integer puntos;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_otorgamiento", nullable = false, updatable = false)
    private LocalDateTime fechaOtorgamiento;
}

