package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramStoryDto(
        String uri,

        @JsonProperty("creation_timestamp")
        Long creationTimestamp,

        String title
) {
}