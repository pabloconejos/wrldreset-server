package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramStringValueDto(
        String href,
        String value,
        Long timestamp
) {
}