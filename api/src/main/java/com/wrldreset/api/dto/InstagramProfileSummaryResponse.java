package com.wrldreset.api.dto;

import com.wrldreset.api.entity.InstagramContentType;

import java.util.Map;
import java.util.UUID;

public record InstagramProfileSummaryResponse(
        UUID profileId,
        String username,
        long totalContents,
        long totalMediaItems,
        Map<InstagramContentType, Long> contentsByType
) {
}
