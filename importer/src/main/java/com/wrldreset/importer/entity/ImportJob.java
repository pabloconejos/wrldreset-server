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
@Table(name = "import_jobs")
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportJobStatus status;

    private Instant startedAt;

    private Instant completedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Integer createdCount;

    private Integer updatedCount;

    private Integer skippedCount;

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