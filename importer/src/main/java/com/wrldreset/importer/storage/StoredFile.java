package com.wrldreset.importer.storage;

import com.wrldreset.importer.entity.MediaType;

public record StoredFile(
        String storagePath,
        String fileName,
        String extension,
        String mimeType,
        MediaType mediaType,
        long sizeBytes,
        String sha256
) {
}