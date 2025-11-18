package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.response.ApiResponse;
import com.eduquestia.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/archivos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:5173", "http://localhost:19006"})
public class FileController {

    private final FileService fileService;
    
    @org.springframework.beans.factory.annotation.Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    /**
     * Subir un archivo (PDF, imagen o video)
     * POST /api/v1/archivos/subir
     */
    @PostMapping("/subir")
    public ResponseEntity<ApiResponse<String>> subirArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("tipo") String tipo) {

        log.info("POST /archivos/subir - Tipo: {}, Nombre: {}", tipo, archivo.getOriginalFilename());

        try {
            // Validar tipo de archivo
            if (!tipo.equals("PDF") && !tipo.equals("IMAGEN") && !tipo.equals("VIDEO")) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Tipo de archivo no válido. Debe ser PDF, IMAGEN o VIDEO")
                );
            }

            // Validar que el archivo no esté vacío
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El archivo no puede estar vacío")
                );
            }

            // Validar tamaño del archivo (máximo 50MB)
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (archivo.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El archivo excede el tamaño máximo permitido (50MB)")
                );
            }

            // Validar extensión del archivo según el tipo
            String originalFilename = archivo.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El nombre del archivo no es válido")
                );
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            
            if (tipo.equals("PDF") && !extension.equals("pdf")) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El archivo debe ser un PDF")
                );
            }
            
            if (tipo.equals("IMAGEN") && !extension.matches("jpg|jpeg|png|gif|webp")) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El archivo debe ser una imagen (jpg, jpeg, png, gif, webp)")
                );
            }
            
            if (tipo.equals("VIDEO") && !extension.matches("mp4|avi|mov|wmv|flv|webm")) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("El archivo debe ser un video (mp4, avi, mov, wmv, flv, webm)")
                );
            }

            // Subir el archivo
            String url = fileService.subirArchivo(archivo, tipo);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(url, "Archivo subido exitosamente")
            );

        } catch (Exception e) {
            log.error("Error al subir archivo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Error al subir el archivo: " + e.getMessage())
            );
        }
    }

    /**
     * Descargar un archivo
     * GET /api/v1/archivos/descargar/{tipo}/{filename}
     */
    @GetMapping("/descargar/{tipo}/{filename:.+}")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable String tipo,
            @PathVariable String filename) {

        try {
            Path filePath = Paths.get(uploadDir, tipo.toLowerCase()).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determinarContentType(tipo, filename);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al descargar archivo: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private String determinarContentType(String tipo, String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        if (tipo.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        } else if (tipo.equalsIgnoreCase("imagen")) {
            switch (extension) {
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "webp":
                    return "image/webp";
                default:
                    return "image/jpeg";
            }
        } else if (tipo.equalsIgnoreCase("video")) {
            switch (extension) {
                case "mp4":
                    return "video/mp4";
                case "avi":
                    return "video/x-msvideo";
                case "mov":
                    return "video/quicktime";
                case "wmv":
                    return "video/x-ms-wmv";
                case "flv":
                    return "video/x-flv";
                case "webm":
                    return "video/webm";
                default:
                    return "video/mp4";
            }
        }
        
        return "application/octet-stream";
    }
}

