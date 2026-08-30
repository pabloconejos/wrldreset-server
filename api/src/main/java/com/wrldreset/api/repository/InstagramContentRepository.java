package com.wrldreset.api.repository;

import com.wrldreset.api.entity.InstagramContent;
import com.wrldreset.api.entity.InstagramProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wrldreset.api.entity.InstagramContentType;
import com.wrldreset.api.dto.ContentTypeCount;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstagramContentRepository extends JpaRepository<InstagramContent, UUID> {

    // CON Spring Data JPA SE TRADUCE EL NOMBRE DE LA FUNCION A UNA SENTENCIA SQL

    @EntityGraph(attributePaths = "mediaItems") // Para esta consulta concreta, carga InstagramContent + mediaItems en la misma operación lógica.
    Page<InstagramContent> findByProfileOrderByCreatedAtInstagramDesc(
            InstagramProfile profile,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "mediaItems")
    Page<InstagramContent> findByProfileAndContentTypeOrderByCreatedAtInstagramDesc(
            InstagramProfile profile,
            InstagramContentType contentType,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "mediaItems")
    Optional<InstagramContent> findByIdAndProfile(UUID id, InstagramProfile profile);

    @Query("""
        select new com.wrldreset.api.dto.ContentTypeCount(c.contentType, count(c))
        from InstagramContent c
        where c.profile = :profile
        group by c.contentType
        """)
    List<ContentTypeCount> countByContentType(InstagramProfile profile);
}