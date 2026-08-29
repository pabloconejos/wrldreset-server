package com.wrldreset.importer.storage;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Component
public class TemporaryDirectoryCleaner {

    public void deleteDirectory(Path directory) throws IOException {
        if (directory == null || Files.notExists(directory)) {
            return;
        }

        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(this::deletePath);
        }
    }

    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new RuntimeException("Could not delete temporary path: " + path, exception);
        }
    }
}