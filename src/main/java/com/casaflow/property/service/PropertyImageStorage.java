package com.casaflow.property.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class PropertyImageStorage {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 8 * 1024 * 1024;
    private final Path root;

    public PropertyImageStorage(@Value("${app.uploads.directory:uploads}") String directory) {
        this.root = Path.of(directory).toAbsolutePath().normalize();
    }

    public String save(UUID propertyId, MultipartFile file) {
        validate(file);
        String extension = extension(file.getContentType());
        String filename = UUID.randomUUID() + extension;
        Path directory = root.resolve("properties").resolve(propertyId.toString()).normalize();
        Path destination = directory.resolve(filename).normalize();

        if (!destination.startsWith(directory)) {
            throw new IllegalArgumentException("Nombre de archivo inválido.");
        }

        try {
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible guardar la imagen.", ex);
        }

        return "/uploads/properties/" + propertyId + "/" + filename;
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/properties/")) {
            return;
        }
        Path target = root.resolve(imageUrl.substring("/uploads/".length())).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Ruta de imagen inválida.");
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible eliminar la imagen.", ex);
        }
    }

    private static void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("La imagen está vacía.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            long sizeMB = file.getSize() / (1024 * 1024);
            throw new IllegalArgumentException(
                String.format("La imagen pesa %d MB. El tamaño máximo es 8 MB.", sizeMB)
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo";
            throw new IllegalArgumentException(
                String.format("El archivo '%s' no es válido. Solo se permiten imágenes JPG, PNG o WebP.", fileName)
            );
        }

        // Validar también por extensión del archivo como segunda capa de seguridad
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String lowerFilename = originalFilename.toLowerCase(Locale.ROOT);
            if (!lowerFilename.endsWith(".jpg") &&
                !lowerFilename.endsWith(".jpeg") &&
                !lowerFilename.endsWith(".png") &&
                !lowerFilename.endsWith(".webp")) {
                throw new IllegalArgumentException(
                    String.format("El archivo '%s' tiene una extensión no permitida. Solo se permiten: .jpg, .jpeg, .png, .webp", originalFilename)
                );
            }
        }
    }

    private static String extension(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
