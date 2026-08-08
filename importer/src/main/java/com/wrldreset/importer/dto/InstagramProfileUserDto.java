package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramProfileUserDto(
        String title,

        @JsonProperty("string_map_data")
        Map<String, InstagramStringValueDto> stringMapData
) {
}