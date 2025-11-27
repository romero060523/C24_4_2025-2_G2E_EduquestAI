package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoEstudianteResponse {
    private UUID id;
    private String codigoCurso;
    private String nombre;
    private String descripcion;
    private String imagenPortada;
    private String fechaInicio;
    private String fechaFin;
    private Boolean activo;

    // Información del profesor
    private String profesorNombre;
    private String profesorEmail;

    // Estadísticas
    private Integer progreso; // Porcentaje de progreso del estudiante
    private Integer misionesCompletadas;
    private Integer totalMisiones;
    private Integer totalEstudiantes;
}

