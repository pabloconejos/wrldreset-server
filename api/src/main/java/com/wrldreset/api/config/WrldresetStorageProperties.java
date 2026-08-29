package com.wrldreset.api.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "wrldreset.storage")
public class WrldresetStorageProperties {

    @NotBlank
    private String mediaPath;

    public Path getMediaPath() {
        return Path.of(mediaPath);
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }
}