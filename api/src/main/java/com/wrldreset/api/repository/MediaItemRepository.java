package com.wrldreset.api.repository;

import com.wrldreset.api.entity.InstagramProfile;
import com.wrldreset.api.entity.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface MediaItemRepository extends JpaRepository<MediaItem, UUID> {

    @Query("""
            select count(m)
            from MediaItem m
            where m.content.profile = :profile
            """)
    long countByProfile(InstagramProfile profile);
}