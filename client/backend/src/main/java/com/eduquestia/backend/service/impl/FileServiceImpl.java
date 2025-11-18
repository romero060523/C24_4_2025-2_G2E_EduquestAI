package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public String subirArchivo(MultipartFile archivo, String tipo) {
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir, tipo.toLowerCase());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = archivo.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            // Guardar el archivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retornar URL relativa que el frontend puede usar
            // El frontend construirá la URL completa con su baseURL
            String url = "http://localhost:8081/api/v1/archivos/descargar/" + tipo.toLowerCase() + "/" + filename;
            
            log.info("Archivo subido exitosamente: {}", filePath);
            log.info("URL generada: {}", url);
            
            return url;

        } catch (IOException e) {
            log.error("Error al guardar archivo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }
}

