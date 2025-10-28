package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MisionProgresoResponse {
    private UUID misionId;
    private String titulo;
    private Integer totalEstudiantes;
    private Integer completados;
    private Integer enProgreso;
    private Integer noIniciados;
    private List<EstudianteProgresoResponse> estudiantes;
}

