package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramIgtvVideosDto(
        @JsonProperty("ig_igtv_media")
        List<InstagramIgtvVideoDto> videos
) {
}