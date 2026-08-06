package com.wrldreset.importer.importer;

import com.wrldreset.importer.config.WrldresetStorageProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class InstagramZipExtractor {

    private final WrldresetStorageProperties storageProperties;

    public InstagramZipExtractor(WrldresetStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public Path extractZipFiles(List<Path> zipFiles) throws IOException {
        Path workingDirectory = createWorkingDirectory();

        for (Path zipFile : zipFiles) {
            extractZipFile(zipFile, workingDirectory);
        }

        return workingDirectory;
    }

    // crea una carpeta temporal por zip
    private Path createWorkingDirectory() throws IOException {
        Path tempPath = storageProperties.getTempPath();
        Files.createDirectories(tempPath);

        Path workingDirectory = tempPath.resolve("instagram-import-" + UUID.randomUUID());
        return Files.createDirectories(workingDirectory);
    }

    // descomprime cada zip y los deja en su carpeta temporal
    private void extractZipFile(Path zipFile, Path targetDirectory) throws IOException {
        try (InputStream inputStream = Files.newInputStream(zipFile);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path outputPath = targetDirectory.resolve(entry.getName()).normalize();

                if (!outputPath.startsWith(targetDirectory)) {
                    throw new IOException("Unsafe ZIP entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                } else {
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(zipInputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        }
    }
}
