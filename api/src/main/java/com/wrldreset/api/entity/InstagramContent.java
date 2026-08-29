package com.wrldreset.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "instagram_contents")
public class InstagramContent {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private InstagramProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstagramContentType contentType;

    @Column(nullable = false, unique = true, length = 1024)
    private String contentSignature;

    @Column(length = 1024)
    private String originalUri;

    @Column(columnDefinition = "TEXT")
    private String title;

    private Instant createdAtInstagram;

    private Instant createdAt;

    private Instant updatedAt;

    @OneToMany(mappedBy = "content")
    @OrderBy("position ASC")
    private List<MediaItem> mediaItems = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public InstagramProfile getProfile() {
        return profile;
    }

    public InstagramContentType getContentType() {
        return contentType;
    }

    public String getContentSignature() {
        return contentSignature;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAtInstagram() {
        return createdAtInstagram;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }
}
