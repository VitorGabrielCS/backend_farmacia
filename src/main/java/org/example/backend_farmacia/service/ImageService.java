package org.example.backend_farmacia.service;

import org.example.backend_farmacia.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;

    private final Path uploadDir;

    public ImageService(@Value("${app.upload-dir:uploads}") String dir) {
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta de uploads: " + uploadDir, e);
        }
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Nenhuma imagem enviada");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidFileException("Imagem muito grande (máxima 5 MB)");
        }

        try {
            byte[] bytes = file.getBytes();

            String extension = detectExtension(bytes);
            if (extension == null) {
                throw new InvalidFileException("Somente imagens nos formatos JPG, PNG e WEBP são aceitas");
            }

            String filename = UUID.randomUUID() + "." + extension;
            Files.write(uploadDir.resolve(filename), bytes);
            return filename;
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar a imagem, ", e);
        }
    }

    private String detectExtension(byte[] b) {
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        return null;
    }
}
