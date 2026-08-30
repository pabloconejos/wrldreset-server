package com.wrldreset.api.dto;

import com.wrldreset.api.entity.InstagramContentType;

public record ContentTypeCount(
        InstagramContentType contentType,
        long count
) {
}
