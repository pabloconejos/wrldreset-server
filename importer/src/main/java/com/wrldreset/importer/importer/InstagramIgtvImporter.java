package com.wrldreset.importer.importer;

import com.wrldreset.importer.dto.InstagramIgtvVideoDto;
import com.wrldreset.importer.dto.InstagramIgtvVideosDto;
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
public class InstagramIgtvImporter {

    private final ObjectMapper objectMapper;
    private final InstagramContentRepository instagramContentRepository;
    private final MediaItemRepository mediaItemRepository;
    private final LocalStorageService localStorageService;

    public InstagramIgtvImporter(
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

    public ImportResult importIgtv(InstagramProfile profile, InstagramExportFiles exportFiles) throws IOException {
        InstagramIgtvVideosDto videosDto = objectMapper.readValue(
                exportFiles.igtvJson().toFile(),
                InstagramIgtvVideosDto.class
        );

        if (videosDto.videos() == null || videosDto.videos().isEmpty()) {
            return ImportResult.empty();
        }

        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;

        for (InstagramIgtvVideoDto video : videosDto.videos()) {
            if (video.media() == null || video.media().isEmpty()) {
                skippedCount++;
                continue;
            }

            boolean created = importIgtvVideo(profile, exportFiles.workingDirectory(), video);

            if (created) {
                createdCount++;
            } else {
                updatedCount++;
            }
        }

        return new ImportResult(createdCount, updatedCount, skippedCount);
    }

    private boolean importIgtvVideo(
            InstagramProfile profile,
            Path workingDirectory,
            InstagramIgtvVideoDto video
    ) throws IOException {
        String contentSignature = buildContentSignature(video);
        InstagramPostMediaDto firstMedia = firstMedia(video);
        Instant createdAtInstagram = toInstant(firstMedia.creationTimestamp());

        InstagramContent content = instagramContentRepository.findByContentSignature(contentSignature)
                .orElse(null);

        boolean created = content == null;

        if (created) {
            content = new InstagramContent();
            content.setProfile(profile);
            content.setContentType(InstagramContentType.IGTV);
            content.setContentSignature(contentSignature);
            content.setOriginalUri(firstMedia.uri());
        }

        content.setTitle(firstMedia.title());
        content.setCreatedAtInstagram(createdAtInstagram);

        InstagramContent savedContent = instagramContentRepository.save(content);

        for (int position = 0; position < video.media().size(); position++) {
            InstagramPostMediaDto media = video.media().get(position);

            if (media.uri() == null || media.uri().isBlank()) {
                continue;
            }

            importIgtvMedia(profile, workingDirectory, savedContent, media, position);
        }

        return created;
    }

    private void importIgtvMedia(
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
                "igtv",
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

    private String buildContentSignature(InstagramIgtvVideoDto video) {
        return "IGTV:" + firstMedia(video).uri();
    }

    private InstagramPostMediaDto firstMedia(InstagramIgtvVideoDto video) {
        return video.media().getFirst();
    }

    private Instant toInstant(Long epochSeconds) {
        if (epochSeconds == null) {
            return null;
        }

        return Instant.ofEpochSecond(epochSeconds);
    }
}