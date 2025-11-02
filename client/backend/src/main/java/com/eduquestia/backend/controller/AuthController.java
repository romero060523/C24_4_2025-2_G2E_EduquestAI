package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.LoginRequestDTO;
import com.eduquestia.backend.dto.LoginResponseDTO;
import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // En producción, especificar orígenes permitidos
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        log.info("POST /auth/login - Email: {}", loginRequest.getEmail());
        
        LoginResponseDTO response = authService.login(loginRequest);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Login exitoso"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", "OK"));
    }
}
