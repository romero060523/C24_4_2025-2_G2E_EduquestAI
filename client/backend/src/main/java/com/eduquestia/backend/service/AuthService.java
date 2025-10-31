package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.LoginRequestDTO;
import com.eduquestia.backend.dto.LoginResponseDTO;
import com.eduquestia.backend.entity.Usuario;
import com.eduquestia.backend.exceptions.AuthenticationException;
import com.eduquestia.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Intento de login para email: {}", loginRequest.getEmail());

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new AuthenticationException("Usuario inactivo");
        }

        // Verificar que sea un profesor
        if (!"profesor".equalsIgnoreCase(usuario.getRol())) {
            throw new AuthenticationException("Acceso denegado. Solo profesores pueden acceder.");
        }

        // Verificar contraseña - soporta tanto BCrypt como Django pbkdf2_sha256
        if (!verifyPassword(loginRequest.getPassword(), usuario.getPassword())) {
            log.warn("Contraseña incorrecta para usuario: {}", loginRequest.getEmail());
            throw new AuthenticationException("Credenciales inválidas");
        }

        log.info("Login exitoso para profesor: {}", usuario.getEmail());

        // Actualizar último acceso
        usuario.setUltimoAcceso(OffsetDateTime.now());
        usuario.setLastLogin(OffsetDateTime.now());
        usuarioRepository.save(usuario);

        // Generar un token simple (en producción usa JWT)
        String token = generateSimpleToken(usuario);

        // Construir respuesta
        return LoginResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .avatarUrl(usuario.getAvatarUrl())
                .token(token)
                .message("Login exitoso")
                .build();
    }

    /**
     * Genera un token simple para desarrollo
     * En producción, usar JWT (JSON Web Token)
     */
    private String generateSimpleToken(Usuario usuario) {
        String payload = usuario.getId() + ":" + usuario.getEmail() + ":" + System.currentTimeMillis();
        return java.util.Base64.getEncoder().encodeToString(payload.getBytes());
    }

    /**
     * Verifica contraseña soportando múltiples formatos:
     * - BCrypt puro (Spring Boot/Django custom): $2a$, $2b$, $2y$
     * - Django bcrypt_pure: bcrypt_pure$$2b$12$...
     * - Django pbkdf2_sha256: pbkdf2_sha256$...
     * - Django bcrypt_sha256: bcrypt_sha256$$2b$12$...
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || plainPassword == null) {
            return false;
        }

        // Extraer el hash BCrypt puro si tiene prefijo de Django
        String bcryptHash = hashedPassword;
        
        if (hashedPassword.startsWith("bcrypt_pure$") || hashedPassword.startsWith("bcrypt_sha256$")) {
            // Formato Django: bcrypt_pure$$2b$12$hash o bcrypt_sha256$$2b$12$hash
            // Extraer solo la parte BCrypt ($2b$12$hash)
            int firstDollar = hashedPassword.indexOf('$');
            if (firstDollar != -1 && firstDollar + 1 < hashedPassword.length()) {
                bcryptHash = hashedPassword.substring(firstDollar + 1); // Saltar "bcrypt_pure$"
                log.info("Hash Django detectado ({}), extrayendo BCrypt puro: {}...", 
                    hashedPassword.substring(0, Math.min(15, hashedPassword.length())),
                    bcryptHash.substring(0, Math.min(20, bcryptHash.length())));
            }
        }
        
        // Detectar formato de hash
        if (bcryptHash.startsWith("$2a$") || 
            bcryptHash.startsWith("$2b$") || 
            bcryptHash.startsWith("$2y$")) {
            // BCrypt hash puro
            log.info("Verificando con BCrypt");
            return BCrypt.checkpw(plainPassword, bcryptHash);
        } 
        else if (hashedPassword.startsWith("pbkdf2_sha256$")) {
            // Django pbkdf2_sha256 hash (fallback para contraseñas antiguas)
            log.info("Verificando contraseña con formato Django pbkdf2_sha256");
            return verifyDjangoPbkdf2Password(plainPassword, hashedPassword);
        }
        else {
            // Formato desconocido
            log.warn("Formato de hash desconocido: {}", hashedPassword.substring(0, Math.min(20, hashedPassword.length())));
            return false;
        }
    }

    /**
     * Verifica contraseñas en formato Django pbkdf2_sha256
     * Formato: pbkdf2_sha256$iterations$salt$hash
     */
    private boolean verifyDjangoPbkdf2Password(String plainPassword, String hashedPassword) {
        try {
            // Parsear el hash de Django
            String[] parts = hashedPassword.split("\\$");
            if (parts.length != 4) {
                log.error("Formato de hash Django inválido: {}", hashedPassword);
                return false;
            }

            // parts[0] = pbkdf2_sha256 (algoritmo)
            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            String expectedHash = parts[3];

            // Generar hash con los mismos parámetros
            PBEKeySpec spec = new PBEKeySpec(
                plainPassword.toCharArray(),
                salt.getBytes(),
                iterations,
                256 // 256 bits = 32 bytes
            );

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String computedHash = Base64.encodeBase64String(hash);

            // Comparar el hash generado con el esperado
            boolean matches = computedHash.equals(expectedHash);
            
            if (matches) {
                log.info("Contraseña Django verificada correctamente");
            } else {
                log.warn("Contraseña Django no coincide");
            }
            
            return matches;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error al verificar contraseña Django: {}", e.getMessage());
            return false;
        }
    }
}
