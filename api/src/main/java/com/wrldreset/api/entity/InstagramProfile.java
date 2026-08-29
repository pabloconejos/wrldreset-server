package com.wrldreset.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "instagram_profiles")
public class InstagramProfile {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String displayName;

    private String website;

    private Boolean privateAccount;

    private Instant createdAt;

    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWebsite() {
        return website;
    }

    public Boolean getPrivateAccount() {
        return privateAccount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}