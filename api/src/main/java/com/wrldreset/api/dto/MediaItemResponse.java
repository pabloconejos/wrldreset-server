package com.wrldreset.api.dto;

import com.wrldreset.api.entity.MediaItem;
import com.wrldreset.api.entity.MediaType;

import java.time.Instant;
import java.util.UUID;

public record MediaItemResponse(
        UUID id,
        Integer position,
        MediaType mediaType,
        String storagePath,
        String mediaUrl,
        String fileName,
        String mimeType,
        Long sizeBytes,
        Instant createdAtInstagram
) {

    public static MediaItemResponse fromEntity(MediaItem mediaItem) {
        return new MediaItemResponse(
                mediaItem.getId(),
                mediaItem.getPosition(),
                mediaItem.getMediaType(),
                mediaItem.getStoragePath(),
                "/api/media/" + mediaItem.getStoragePath(),
                mediaItem.getFileName(),
                mediaItem.getMimeType(),
                mediaItem.getSizeBytes(),
                mediaItem.getCreatedAtInstagram()
        );
    }
}
