package com.wrldreset.importer.storage;

import com.wrldreset.importer.config.WrldresetStorageProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.UUID;

@Component
public class LocalStorageService {

    private final WrldresetStorageProperties storageProperties;
    private final MediaTypeDetector mediaTypeDetector;

    public LocalStorageService(
            WrldresetStorageProperties storageProperties,
            MediaTypeDetector mediaTypeDetector
    ) {
        this.storageProperties = storageProperties;
        this.mediaTypeDetector = mediaTypeDetector;
    }

    // COPIA EL FILE DE LA RUTA DE TEMP A LA RUTA CORRESPONDIENTE EN /STORAGE
    public StoredFile storeMedia(
            UUID profileId,
            String contentType,
            Path sourceFile,
            String originalUri,
            Instant createdAt
    ) throws IOException {
        if (!Files.isRegularFile(sourceFile)) {
            throw new IllegalStateException("Media file not found: " + sourceFile);
        }

        String fileName = mediaTypeDetector.fileNameOf(originalUri);
        String extension = mediaTypeDetector.extensionOf(originalUri);

        Instant date = createdAt != null ? createdAt : Instant.now();
        String year = String.valueOf(date.atZone(ZoneOffset.UTC).getYear());
        String month = String.format("%02d", date.atZone(ZoneOffset.UTC).getMonthValue());

        String relativeStoragePath = Path.of(
                "profiles",
                profileId.toString(),
                contentType.toLowerCase(),
                year,
                month,
                fileName
        ).toString();

        Path targetFile = storageProperties.getMediaPath().resolve(relativeStoragePath);
        Files.createDirectories(targetFile.getParent());

        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

        long sizeBytes = Files.size(targetFile);
        String sha256 = sha256(targetFile);

        return new StoredFile(
                relativeStoragePath,
                fileName,
                extension,
                mediaTypeDetector.mimeTypeOf(originalUri),
                mediaTypeDetector.detect(originalUri),
                sizeBytes,
                sha256
        );
    }

    private String sha256(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream inputStream = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}