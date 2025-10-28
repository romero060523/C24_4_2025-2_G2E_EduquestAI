package com.eduquestia.backend.entity;

import com.eduquestia.backend.entity.enums.TipoContenido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contenido_mision", schema = "grupo_03")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoMision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", nullable = false)
    private Mision mision;

    // ENUM: 'texto', 'video', 'pdf', 'link', 'imagen'
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contenido", nullable = false, length = 20)
    private TipoContenido tipoContenido;

    @Column(length = 100)
    private String titulo;

    @Column(name = "contenido_url", length = 500)
    private String contenidoUrl;

    @Column(name = "contenido_texto", columnDefinition = "TEXT")
    private String contenidoTexto;

    @Column(nullable = false)
    private Integer orden = 0;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
