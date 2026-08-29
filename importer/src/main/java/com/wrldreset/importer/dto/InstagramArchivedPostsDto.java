package com.wrldreset.importer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstagramArchivedPostsDto(
        @JsonProperty("ig_archived_post_media")
        List<InstagramArchivedPostDto> archivedPosts
) {
}