package com.wrldreset.importer.repository;

import com.wrldreset.importer.entity.InstagramContent;
import com.wrldreset.importer.entity.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaItemRepository extends JpaRepository<MediaItem, UUID> {

    Optional<MediaItem> findByContentAndPosition(InstagramContent content, Integer position);
}