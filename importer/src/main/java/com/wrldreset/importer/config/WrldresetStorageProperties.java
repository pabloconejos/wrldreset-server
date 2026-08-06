package com.wrldreset.importer.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "wrldreset.storage")
public class WrldresetStorageProperties {

    // CLASE PARA GUARDAR LAS RUTAS DE storage/temp, storage/imports, storage/media y no usar strings por el codigo
    @NotNull
    private Path importsPath;

    @NotNull
    private Path mediaPath;

    @NotNull
    private Path tempPath;

    public Path getImportsPath() {
        return importsPath;
    }

    public void setImportsPath(Path importsPath) {
        this.importsPath = importsPath;
    }

    public Path getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(Path mediaPath) {
        this.mediaPath = mediaPath;
    }

    public Path getTempPath() {
        return tempPath;
    }

    public void setTempPath(Path tempPath) {
        this.tempPath = tempPath;
    }
}
