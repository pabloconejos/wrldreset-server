package com.wrldreset.api.controller;

import com.wrldreset.api.dto.InstagramContentResponse;
import com.wrldreset.api.entity.InstagramProfile;
import com.wrldreset.api.exception.ResourceNotFoundException;
import com.wrldreset.api.repository.InstagramContentRepository;
import com.wrldreset.api.repository.InstagramProfileRepository;
import com.wrldreset.api.entity.InstagramContent;
import com.wrldreset.api.entity.InstagramContentType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;


import java.util.UUID;

@RestController
public class InstagramContentController {

    private final InstagramProfileRepository instagramProfileRepository;
    private final InstagramContentRepository instagramContentRepository;

    public InstagramContentController(
            InstagramProfileRepository instagramProfileRepository,
            InstagramContentRepository instagramContentRepository
    ) {
        this.instagramProfileRepository = instagramProfileRepository;
        this.instagramContentRepository = instagramContentRepository;
    }

    @GetMapping("/api/profiles/{profileId}/contents")
    public Page<InstagramContentResponse> findByProfile(
            @PathVariable UUID profileId,
            @RequestParam(required = false) InstagramContentType type,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        InstagramProfile profile = instagramProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram profile not found: " + profileId));

        Page<InstagramContent> contents;

        if (type == null) {
            contents = instagramContentRepository.findByProfileOrderByCreatedAtInstagramDesc(profile, pageable);
        } else {
            contents = instagramContentRepository.findByProfileAndContentTypeOrderByCreatedAtInstagramDesc(profile, type, pageable);
        }

        return contents.map(InstagramContentResponse::fromEntity);
    }

    @GetMapping("/api/profiles/{profileId}/contents/{contentId}")
    public InstagramContentResponse findById(
            @PathVariable UUID profileId,
            @PathVariable UUID contentId
    ) {
        InstagramProfile profile = instagramProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram profile not found: " + profileId));

        return instagramContentRepository.findByIdAndProfile(contentId, profile)
                .map(InstagramContentResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram content not found: " + contentId));
    }
}