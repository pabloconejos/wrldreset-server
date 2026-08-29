package com.wrldreset.api.controller;

import com.wrldreset.api.config.WrldresetStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class MediaController {

    private final WrldresetStorageProperties storageProperties;

    public MediaController(WrldresetStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @GetMapping("/api/media/{*storagePath}")
    public ResponseEntity<Resource> getMedia(@PathVariable String storagePath) throws MalformedURLException {
        String cleanStoragePath = storagePath;

        if (cleanStoragePath.startsWith("/")) {
            cleanStoragePath = cleanStoragePath.substring(1);
        }

        Path mediaRoot = storageProperties.getMediaPath().toAbsolutePath().normalize();
        Path mediaFile = mediaRoot.resolve(cleanStoragePath).normalize();

        if (!mediaFile.startsWith(mediaRoot)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.isRegularFile(mediaFile)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(mediaFile.toUri());

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
    }
}