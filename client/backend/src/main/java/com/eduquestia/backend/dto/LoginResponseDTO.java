package com.eduquestia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    
    private UUID id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String rol;
    private String avatarUrl;
    private String token;
    
    // Mensaje opcional
    private String message;
}
