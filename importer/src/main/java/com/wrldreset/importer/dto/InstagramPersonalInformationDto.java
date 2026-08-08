package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramPersonalInformationDto(
        @JsonProperty("profile_user")
        List<InstagramProfileUserDto> profileUser
) {
}