package com.wrldreset.api.dto;

import com.wrldreset.api.entity.InstagramContent;
import com.wrldreset.api.entity.InstagramContentType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InstagramContentResponse(
        UUID id,
        InstagramContentType contentType,
        String title,
        Instant createdAtInstagram,
        List<MediaItemResponse> mediaItems
) {

    public static InstagramContentResponse fromEntity(InstagramContent content) {
        return new InstagramContentResponse(
                content.getId(),
                content.getContentType(),
                content.getTitle(),
                content.getCreatedAtInstagram(),
                content.getMediaItems()
                        .stream()
                        .map(MediaItemResponse::fromEntity)
                        .toList()
        );
    }
}