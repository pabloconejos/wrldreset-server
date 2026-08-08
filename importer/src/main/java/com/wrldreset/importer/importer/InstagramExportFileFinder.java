package com.wrldreset.importer.importer;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

// ESTA CLASE COMPRUEBA QUE EL UNZIP CONTIENE LAS RUTAS NECESARIAS PARA LA IMPORTACIÓN
@Component
public class InstagramExportFileFinder {

    public InstagramExportFiles findExportFiles(Path workingDirectory) {
        return new InstagramExportFiles(
                workingDirectory,
                requireFile(workingDirectory, InstagramExportKnownFiles.PERSONAL_INFORMATION),
                requireFile(workingDirectory, InstagramExportKnownFiles.STORIES),
                requireFile(workingDirectory, InstagramExportKnownFiles.POSTS),
                requireFile(workingDirectory, InstagramExportKnownFiles.POSTS_METADATA),
                requireFile(workingDirectory, InstagramExportKnownFiles.REELS),
                requireFile(workingDirectory, InstagramExportKnownFiles.IGTV),
                requireFile(workingDirectory, InstagramExportKnownFiles.ARCHIVED_POSTS)
        );
    }

    private Path requireFile(Path root, String relativePath) {
        Path file = root.resolve(relativePath);

        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("Required Instagram export file not found: " + file);
        }

        return file;
    }
}