package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramPostMediaDto(
        String uri,

        @JsonProperty("creation_timestamp")
        Long creationTimestamp,

        String title
) {
}