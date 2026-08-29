package com.wrldreset.api.controller;

import com.wrldreset.api.dto.InstagramContentResponse;
import com.wrldreset.api.entity.InstagramProfile;
import com.wrldreset.api.repository.InstagramContentRepository;
import com.wrldreset.api.repository.InstagramProfileRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public List<InstagramContentResponse> findByProfile(@PathVariable UUID profileId) {
        InstagramProfile profile = instagramProfileRepository.findById(profileId)
                .orElseThrow();

        return instagramContentRepository.findByProfileOrderByCreatedAtInstagramDesc(profile)
                .stream()
                .map(InstagramContentResponse::fromEntity)
                .toList();
    }
}