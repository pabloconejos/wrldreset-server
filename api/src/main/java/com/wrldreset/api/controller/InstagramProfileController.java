package com.wrldreset.api.controller;

import com.wrldreset.api.entity.InstagramProfile;
import com.wrldreset.api.exception.ResourceNotFoundException;
import com.wrldreset.api.repository.InstagramProfileRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.wrldreset.api.dto.ContentTypeCount;
import com.wrldreset.api.dto.InstagramProfileSummaryResponse;
import com.wrldreset.api.entity.InstagramContentType;
import com.wrldreset.api.exception.ResourceNotFoundException;
import com.wrldreset.api.repository.InstagramContentRepository;
import com.wrldreset.api.repository.MediaItemRepository;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
public class InstagramProfileController {

    private final InstagramProfileRepository instagramProfileRepository;
    private final InstagramContentRepository instagramContentRepository;
    private final MediaItemRepository mediaItemRepository;

    public InstagramProfileController(
            InstagramProfileRepository instagramProfileRepository,
            InstagramContentRepository instagramContentRepository,
            MediaItemRepository mediaItemRepository
    ) {
        this.instagramProfileRepository = instagramProfileRepository;
        this.instagramContentRepository = instagramContentRepository;
        this.mediaItemRepository = mediaItemRepository;
    }

    @GetMapping("/api/profiles")
    public List<InstagramProfile> findAll() {
        return instagramProfileRepository.findAll();
    }

    @GetMapping("/api/profiles/{id}")
    public InstagramProfile findById(@PathVariable UUID id) {
        return instagramProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram profile not found: " + id));
    }

    @GetMapping("/api/profiles/{id}/summary")
    public InstagramProfileSummaryResponse summary(@PathVariable UUID id) {
        InstagramProfile profile = instagramProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instagram profile not found: " + id));

        Map<InstagramContentType, Long> contentsByType = new EnumMap<>(InstagramContentType.class);

        for (ContentTypeCount contentTypeCount : instagramContentRepository.countByContentType(profile)) {
            contentsByType.put(contentTypeCount.contentType(), contentTypeCount.count());
        }

        long totalContents = contentsByType.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        long totalMediaItems = mediaItemRepository.countByProfile(profile);

        return new InstagramProfileSummaryResponse(
                profile.getId(),
                profile.getUsername(),
                totalContents,
                totalMediaItems,
                contentsByType
        );
    }
}
