package com.wrldreset.importer.importer;

import com.wrldreset.importer.dto.InstagramPostDto;
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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Component
public class InstagramPostsImporter {

    private final ObjectMapper objectMapper;
    private final InstagramContentRepository instagramContentRepository;
    private final MediaItemRepository mediaItemRepository;
    private final LocalStorageService localStorageService;

    public InstagramPostsImporter(
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

    public ImportResult importPosts(InstagramProfile profile, InstagramExportFiles exportFiles) throws IOException {
        List<InstagramPostDto> posts = objectMapper.readValue(
                exportFiles.postsJson().toFile(),
                new TypeReference<List<InstagramPostDto>>() {
                }
        );

        if (posts == null || posts.isEmpty()) {
            return ImportResult.empty();
        }

        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;

        for (InstagramPostDto post : posts) {
            if (post.media() == null || post.media().isEmpty()) {
                skippedCount++;
                continue;
            }

            boolean created = importPost(profile, exportFiles.workingDirectory(), post);

            if (created) {
                createdCount++;
            } else {
                updatedCount++;
            }
        }

        return new ImportResult(createdCount, updatedCount, skippedCount);
    }

    private boolean importPost(
            InstagramProfile profile,
            Path workingDirectory,
            InstagramPostDto post
    ) throws IOException {
        String contentSignature = buildContentSignature(post);
        Instant createdAtInstagram = postCreatedAt(post);

        InstagramContent content = instagramContentRepository.findByContentSignature(contentSignature)
                .orElse(null);

        boolean created = content == null;

        if (created) {
            content = new InstagramContent();
            content.setProfile(profile);
            content.setContentType(InstagramContentType.POST);
            content.setContentSignature(contentSignature);
            content.setOriginalUri(firstMediaUri(post));
        }

        content.setTitle(post.title());
        content.setCreatedAtInstagram(createdAtInstagram);

        InstagramContent savedContent = instagramContentRepository.save(content);

        for (int position = 0; position < post.media().size(); position++) {
            InstagramPostMediaDto media = post.media().get(position);

            if (media.uri() == null || media.uri().isBlank()) {
                continue;
            }

            importPostMedia(profile, workingDirectory, savedContent, media, position);
        }

        return created;
    }

    private void importPostMedia(
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
                "post",
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

    private String buildContentSignature(InstagramPostDto post) {
        return "POST:" + firstMediaUri(post);
    }

    private String firstMediaUri(InstagramPostDto post) {
        return post.media().getFirst().uri();
    }

    private Instant postCreatedAt(InstagramPostDto post) {
        if (post.creationTimestamp() != null) {
            return toInstant(post.creationTimestamp());
        }

        InstagramPostMediaDto firstMedia = post.media().getFirst();
        return toInstant(firstMedia.creationTimestamp());
    }

    private Instant toInstant(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }

        return Instant.ofEpochSecond(epochSeconds);
    }
}