package com.wrldreset.importer.importer;

import com.wrldreset.importer.config.WrldresetStorageProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class InstagramZipFinder {

    private final WrldresetStorageProperties storageProperties;

    public InstagramZipFinder(WrldresetStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public List<Path> findZipFiles() throws IOException {
        Path importsPath = storageProperties.getImportsPath();

        if (!Files.exists(importsPath)) {
            throw new IllegalStateException("Imports path does not exist: " + importsPath);
        }

        try (var files = Files.list(importsPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(this::isZipFile)
                    .sorted()
                    .toList();
        }
    }

    private boolean isZipFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".zip");
    }
}