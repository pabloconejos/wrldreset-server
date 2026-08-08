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
@Table(name = "instagram_contents")
public class InstagramContent {

    // Una unidad visible del archivo.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @PrePersist
    void beforeCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = Instant.now();
    }
}