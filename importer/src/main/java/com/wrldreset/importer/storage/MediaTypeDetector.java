package com.wrldreset.importer.storage;

import com.wrldreset.importer.entity.MediaType;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MediaTypeDetector {

    public MediaType detect(String uri) {
        String extension = extensionOf(uri);

        return switch (extension) {
            case "jpg", "jpeg", "png", "webp" -> MediaType.IMAGE;
            case "mp4", "mov" -> MediaType.VIDEO;
            case "srt" -> MediaType.SUBTITLE;
            default -> MediaType.UNKNOWN;
        };
    }

    public String extensionOf(String uri) {
        if (uri == null || uri.isBlank()) {
            return "";
        }

        int lastDot = uri.lastIndexOf('.');

        if (lastDot < 0 || lastDot == uri.length() - 1) {
            return "";
        }

        return uri.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    public String fileNameOf(String uri) {
        if (uri == null || uri.isBlank()) {
            return "";
        }

        int lastSlash = uri.lastIndexOf('/');

        if (lastSlash < 0 || lastSlash == uri.length() - 1) {
            return uri;
        }

        return uri.substring(lastSlash + 1);
    }

    public String mimeTypeOf(String uri) {
        String extension = extensionOf(uri);

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "mp4" -> "video/mp4";
            case "mov" -> "video/quicktime";
            case "srt" -> "application/x-subrip";
            default -> "application/octet-stream";
        };
    }
}