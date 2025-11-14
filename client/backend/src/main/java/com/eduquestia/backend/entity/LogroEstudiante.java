package com.eduquestia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logros_estudiante", schema = "grupo_03",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "logro_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogroEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logro_id", nullable = false)
    private Logro logro;

    @CreationTimestamp
    @Column(name = "fecha_obtenido", nullable = false, updatable = false)
    private LocalDateTime fechaObtenido;
}

