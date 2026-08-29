package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramReelsDto(
        @JsonProperty("ig_reels_media")
        List<InstagramReelDto> reels
) {
}