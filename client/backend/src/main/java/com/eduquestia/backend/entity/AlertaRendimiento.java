package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.EstadoAlerta;
import com.eduquestia.backend.entity.enums.TipoAlerta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerta_rendimiento", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaRendimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracion_id")
    private ConfiguracionAlerta configuracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAlerta tipo; // INACTIVIDAD, BAJO_RENDIMIENTO, MISIONES_PENDIENTES

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion; // ej: "Sin actividad por 7 dias"

    @Column(name = "datos_contexto", columnDefinition = "TEXT")
    private String datosContexto; //JSON con metricas

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAlerta estado = EstadoAlerta.ACTIVA; // ACTIVA, RESUELTA, IGNORADA

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

}
