package com.wrldreset.importer.importer;

import com.wrldreset.importer.dto.InstagramStoriesDto;
import com.wrldreset.importer.dto.InstagramStoryDto;
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
import java.util.List;

@Component
public class InstagramStoriesImporter {

    private final ObjectMapper objectMapper;
    private final InstagramContentRepository instagramContentRepository;
    private final MediaItemRepository mediaItemRepository;
    private final LocalStorageService localStorageService;

    public InstagramStoriesImporter(
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

    public ImportResult importStories(InstagramProfile profile, InstagramExportFiles exportFiles) throws IOException {
        InstagramStoriesDto storiesDto =
                objectMapper.readValue(exportFiles.storiesJson().toFile(), InstagramStoriesDto.class);

        List<InstagramStoryDto> stories = storiesDto.stories();

        if (stories == null || stories.isEmpty()) {
            return ImportResult.empty();
        }

        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;

        for (InstagramStoryDto story : stories) {
            if (story.uri() == null || story.uri().isBlank()) {
                skippedCount++;
                continue;
            }

            boolean created = importStory(profile, exportFiles.workingDirectory(), story);

            if (created) {
                createdCount++;
            } else {
                updatedCount++;
            }
        }

        return new ImportResult(createdCount, updatedCount, skippedCount);
    }

    private boolean importStory(
            InstagramProfile profile,
            Path workingDirectory,
            InstagramStoryDto story
    ) throws IOException {
        String contentSignature = "STORY:" + story.uri();
        Instant createdAtInstagram = toInstant(story.creationTimestamp());

        InstagramContent content = instagramContentRepository.findByContentSignature(contentSignature)
                .orElse(null);

        boolean created = content == null;

        if (created) {
            content = new InstagramContent();
            content.setProfile(profile);
            content.setContentType(InstagramContentType.STORY);
            content.setContentSignature(contentSignature);
            content.setOriginalUri(story.uri());
        }

        content.setTitle(story.title());
        content.setCreatedAtInstagram(createdAtInstagram);

        InstagramContent savedContent = instagramContentRepository.save(content);

        MediaItem mediaItem = mediaItemRepository.findByContentAndPosition(savedContent, 0)
                .orElse(null);

        if (mediaItem == null) {
            mediaItem = new MediaItem();
            mediaItem.setContent(savedContent);
            mediaItem.setPosition(0);
        }

        Path sourceMediaFile = workingDirectory.resolve(story.uri());

        StoredFile storedFile = localStorageService.storeMedia(
                profile.getId(),
                "story",
                sourceMediaFile,
                story.uri(),
                createdAtInstagram
        );

        mediaItem.setMediaType(storedFile.mediaType());
        mediaItem.setOriginalUri(story.uri());
        mediaItem.setStoragePath(storedFile.storagePath());
        mediaItem.setFileName(storedFile.fileName());
        mediaItem.setExtension(storedFile.extension());
        mediaItem.setMimeType(storedFile.mimeType());
        mediaItem.setSizeBytes(storedFile.sizeBytes());
        mediaItem.setSha256(storedFile.sha256());
        mediaItem.setCreatedAtInstagram(createdAtInstagram);

        mediaItemRepository.save(mediaItem);
        return created;
    }

    private Instant toInstant(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }

        return Instant.ofEpochSecond(epochSeconds);
    }
}


/*

1. genera contentSignature
2. crea/actualiza InstagramContent
3. busca el archivo real en workingDirectory
4. copia el archivo a storage/media
5. crea/actualiza MediaItem

 */