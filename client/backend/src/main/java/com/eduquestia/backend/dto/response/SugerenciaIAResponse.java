package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaIAResponse {
    private List<SugerenciaMeta> metasSugeridas;
    private List<SugerenciaRecompensa> recompensasSugeridas;
    private String analisisProgreso;
    private String mensajeMotivacional;
}

