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
public class RankingResponse {
    private UUID cursoId;
    private String cursoNombre;
    private List<RankingEstudianteResponse> estudiantes;
    private Integer totalEstudiantes;
}

