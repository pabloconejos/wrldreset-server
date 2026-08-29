package com.wrldreset.importer.importer;

import com.wrldreset.importer.dto.InstagramArchivedPostDto;
import com.wrldreset.importer.dto.InstagramArchivedPostsDto;
import com.wrldreset.importer.dto.InstagramPostMediaDto;
import com.wrldreset.importer.entity.InstagramContent;
import com.wrldreset.importer.entity.InstagramContentType;
import com.wrldreset.importer.entity.InstagramProfile;
import com.wrldreset.importer.entity.MediaItem;
import com.wrldreset.importer.repository.InstagramContentRepository;
import com.wrldreset.importer.repository.MediaItemRepository;
import com.wrldreset.importer.storage.LocalStorageService;
import com.wrldreset.importer.storage.StoredFile;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

@Component
public class InstagramArchivedPostsImporter {

    private final ObjectMapper objectMapper;
    private final InstagramContentRepository instagramContentRepository;
    private final MediaItemRepository mediaItemRepository;
    private final LocalStorageService localStorageService;

    public InstagramArchivedPostsImporter(
            ObjectMapper objectMapper,
            InstagramContentRepository instagramContentRepository,
            MediaItemRepository mediaItemRepository,
            LocalStorageService localStorageService
    ) {
        this.objectMapper = objectMapper;
        this.instagramContentRepository = instagramContentRepository;
        this.mediaItemRepository = mediaItemRepository;
        this.localStorageService = localStorageService;
    }

    public ImportResult importArchivedPosts(InstagramProfile profile, InstagramExportFiles exportFiles) throws IOException {
        InstagramArchivedPostsDto archivedPostsDto = objectMapper.readValue(
                exportFiles.archivedPostsJson().toFile(),
                InstagramArchivedPostsDto.class
        );

        if (archivedPostsDto.archivedPosts() == null || archivedPostsDto.archivedPosts().isEmpty()) {
            return ImportResult.empty();
        }

        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;

        for (InstagramArchivedPostDto archivedPost : archivedPostsDto.archivedPosts()) {
            if (archivedPost.media() == null || archivedPost.media().isEmpty()) {
                skippedCount++;
                continue;
            }

            boolean created = importArchivedPost(profile, exportFiles.workingDirectory(), archivedPost);

            if (created) {
                createdCount++;
            } else {
                updatedCount++;
            }
        }

        return new ImportResult(createdCount, updatedCount, skippedCount);
    }

    private boolean importArchivedPost(
            InstagramProfile profile,
            Path workingDirectory,
            InstagramArchivedPostDto archivedPost
    ) throws IOException {
        String contentSignature = buildContentSignature(archivedPost);
        InstagramPostMediaDto firstMedia = firstMedia(archivedPost);
        Instant createdAtInstagram = toInstant(firstMedia.creationTimestamp());

        InstagramContent content = instagramContentRepository.findByContentSignature(contentSignature)
                .orElse(null);

        boolean created = content == null;

        if (created) {
            content = new InstagramContent();
            content.setProfile(profile);
            content.setContentType(InstagramContentType.ARCHIVED_POST);
            content.setContentSignature(contentSignature);
            content.setOriginalUri(firstMedia.uri());
        }

        content.setTitle(firstMedia.title());
        content.setCreatedAtInstagram(createdAtInstagram);

        InstagramContent savedContent = instagramContentRepository.save(content);

        for (int position = 0; position < archivedPost.media().size(); position++) {
            InstagramPostMediaDto media = archivedPost.media().get(position);

            if (media.uri() == null || media.uri().isBlank()) {
                continue;
            }

            importArchivedPostMedia(profile, workingDirectory, savedContent, media, position);
        }

        return created;
    }

    private void importArchivedPostMedia(
            InstagramProfile profile,
            Path workingDirectory,
            InstagramContent content,
            InstagramPostMediaDto media,
            int position
    ) throws IOException {
        MediaItem mediaItem = mediaItemRepository.findByContentAndPosition(content, position)
                .orElse(null);

        if (mediaItem == null) {
            mediaItem = new MediaItem();
            mediaItem.setContent(content);
            mediaItem.setPosition(position);
        }

        Instant createdAtInstagram = toInstant(media.creationTimestamp());
        Path sourceMediaFile = workingDirectory.resolve(media.uri());

        StoredFile storedFile = localStorageService.storeMedia(
                profile.getId(),
                "archived-post",
                sourceMediaFile,
                media.uri(),
                createdAtInstagram
        );

        mediaItem.setMediaType(storedFile.mediaType());
        mediaItem.setOriginalUri(media.uri());
        mediaItem.setStoragePath(storedFile.storagePath());
        mediaItem.setFileName(storedFile.fileName());
        mediaItem.setExtension(storedFile.extension());
        mediaItem.setMimeType(storedFile.mimeType());
        mediaItem.setSizeBytes(storedFile.sizeBytes());
        mediaItem.setSha256(storedFile.sha256());
        mediaItem.setCreatedAtInstagram(createdAtInstagram);

        mediaItemRepository.save(mediaItem);
    }

    private String buildContentSignature(InstagramArchivedPostDto archivedPost) {
        return "ARCHIVED_POST:" + firstMedia(archivedPost).uri();
    }

    private InstagramPostMediaDto firstMedia(InstagramArchivedPostDto archivedPost) {
        return archivedPost.media().getFirst();
    }

    private Instant toInstant(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }

        return Instant.ofEpochSecond(epochSeconds);
    }
}