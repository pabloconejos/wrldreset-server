package com.wrldreset.api.repository;

import com.wrldreset.api.entity.InstagramContent;
import com.wrldreset.api.entity.InstagramProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.UUID;

public interface InstagramContentRepository extends JpaRepository<InstagramContent, UUID> {

    @EntityGraph(attributePaths = "mediaItems") // Para esta consulta concreta, carga InstagramContent + mediaItems en la misma operación lógica.
    List<InstagramContent> findByProfileOrderByCreatedAtInstagramDesc(InstagramProfile profile);
}