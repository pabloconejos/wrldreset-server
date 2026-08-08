package com.wrldreset.importer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "media_items")
public class MediaItem { // Un archivo físico asociado a un contenido.

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @PrePersist
    void beforeCreate() {
        createdAt = Instant.now();
    }
}