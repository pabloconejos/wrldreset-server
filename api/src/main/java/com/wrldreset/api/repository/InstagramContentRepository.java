package com.wrldreset.api.repository;

import com.wrldreset.api.entity.InstagramContent;
import com.wrldreset.api.entity.InstagramProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstagramContentRepository extends JpaRepository<InstagramContent, UUID> {

    @EntityGraph(attributePaths = "mediaItems") // Para esta consulta concreta, carga InstagramContent + mediaItems en la misma operación lógica.
    Page<InstagramContent> findByProfileOrderByCreatedAtInstagramDesc(
            InstagramProfile profile,
            Pageable pageable
    );
}