package com.wrldreset.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media_items")
public class MediaItem {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private InstagramContent content;

    @Column(nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false, length = 1024)
    private String originalUri;

    @Column(nullable = false, length = 1024)
    private String storagePath;

    @Column(nullable = false, length = 512)
    private String fileName;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String mimeType;

    private Long sizeBytes;

    private String sha256;

    private Instant createdAtInstagram;

    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public InstagramContent getContent() {
        return content;
    }

    public Integer getPosition() {
        return position;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public Instant getCreatedAtInstagram() {
        return createdAtInstagram;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}