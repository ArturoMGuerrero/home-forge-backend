package com.casaflow.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class DocumentStorage {
    private final Path root;

    public DocumentStorage(@Value("${app.uploads.directory:uploads}") String uploadsDirectory) {
        this.root = Path.of(uploadsDirectory).toAbsolutePath().normalize().resolve("documents");
    }

    public String store(UUID companyId, MultipartFile file) {
        try {
            String original = file.getOriginalFilename() == null ? "document" : Path.of(file.getOriginalFilename()).getFileName().toString();
            String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            Path directory = root.resolve(companyId.toString());
            Files.createDirectories(directory);
            Path target = directory.resolve(UUID.randomUUID() + extension).normalize();
            if (!target.startsWith(directory)) throw new IllegalArgumentException("Nombre de archivo inválido.");
            file.transferTo(target);
            return target.toString();
        } catch (IOException exception) {
            throw new IllegalArgumentException("No fue posible guardar el documento.");
        }
    }

    public Path resolve(String filePath) {
        Path path = Path.of(filePath).toAbsolutePath().normalize();
        if (!path.startsWith(root)) throw new IllegalArgumentException("Ruta de documento inválida.");
        return path;
    }

    public void delete(String filePath) {
        try { Files.deleteIfExists(resolve(filePath)); } catch (IOException ignored) {}
    }
}
