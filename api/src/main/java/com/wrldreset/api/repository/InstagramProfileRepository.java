package com.wrldreset.api.repository;

import com.wrldreset.api.entity.InstagramProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstagramProfileRepository extends JpaRepository<InstagramProfile, UUID> {

    Optional<InstagramProfile> findByUsername(String username);
}
