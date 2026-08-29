package com.wrldreset.api.controller;

import com.wrldreset.api.entity.InstagramProfile;
import com.wrldreset.api.repository.InstagramProfileRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

import java.util.List;

@RestController
public class InstagramProfileController {

    private final InstagramProfileRepository instagramProfileRepository;

    public InstagramProfileController(InstagramProfileRepository instagramProfileRepository) {
        this.instagramProfileRepository = instagramProfileRepository;
    }

    @GetMapping("/api/profiles")
    public List<InstagramProfile> findAll() {
        return instagramProfileRepository.findAll();
    }

    @GetMapping("/api/profiles/{id}")
    public InstagramProfile findById(@PathVariable UUID id) {
        return instagramProfileRepository.findById(id)
                .orElseThrow();
    }
}
