package com.wrldreset.importer.importer;

import com.wrldreset.importer.config.WrldresetStorageProperties;
import com.wrldreset.importer.entity.ImportJob;
import com.wrldreset.importer.entity.InstagramProfile;
import com.wrldreset.importer.storage.StoredFile;
import org.springframework.stereotype.Component;
import com.wrldreset.importer.storage.LocalStorageService;
import com.wrldreset.importer.storage.StoredFile;

import java.nio.file.Path;
import java.util.List;

@Component
public class InstagramImportWorkflow {

    private final WrldresetStorageProperties storageProperties;
    private final InstagramZipFinder instagramZipFinder;
    private final InstagramZipExtractor instagramZipExtractor;
    private final InstagramExportFileFinder instagramExportFileFinder;
    private final InstagramProfileImporter instagramProfileImporter;
    private final ImportJobService importJobService;
    private final InstagramStoriesImporter instagramStoriesImporter;
    private final InstagramPostsImporter instagramPostsImporter;

    public InstagramImportWorkflow(
            WrldresetStorageProperties storageProperties,
            InstagramZipFinder instagramZipFinder,
            InstagramZipExtractor instagramZipExtractor,
            InstagramExportFileFinder instagramExportFileFinder,
            InstagramProfileImporter instagramProfileImporter,
            ImportJobService importJobService,
            InstagramStoriesImporter instagramStoriesImporter,
            InstagramPostsImporter instagramPostsImporter
    ) {
        this.storageProperties = storageProperties;
        this.instagramZipFinder = instagramZipFinder;
        this.instagramZipExtractor = instagramZipExtractor;
        this.instagramExportFileFinder = instagramExportFileFinder;
        this.instagramProfileImporter = instagramProfileImporter;
        this.importJobService = importJobService;
        this.instagramStoriesImporter = instagramStoriesImporter;
        this.instagramPostsImporter = instagramPostsImporter;
    }

    public void importFromConfiguredImportsFolder() throws Exception {
        ImportJob importJob = importJobService.start();

        try {
            System.out.println("Import job started:");
            System.out.println("id: " + importJob.getId());

            System.out.println("wrldReset importer storage paths:");
            System.out.println("imports: " + storageProperties.getImportsPath());
            System.out.println("media: " + storageProperties.getMediaPath());
            System.out.println("temp: " + storageProperties.getTempPath());

            System.out.println("Instagram ZIP files:");
            List<Path> zipFiles = instagramZipFinder.findZipFiles();
            zipFiles.forEach(path -> System.out.println("- " + path));

            Path workingDirectory = instagramZipExtractor.extractZipFiles(zipFiles);

            System.out.println("Extracted Instagram export to:");
            System.out.println(workingDirectory);

            InstagramExportFiles exportFiles = instagramExportFileFinder.findExportFiles(workingDirectory);

            System.out.println("Instagram export files found:");
            System.out.println("personal information: " + exportFiles.personalInformationJson());
            System.out.println("stories: " + exportFiles.storiesJson());
            System.out.println("posts: " + exportFiles.postsJson());
            System.out.println("posts metadata: " + exportFiles.postsMetadataJson());
            System.out.println("reels: " + exportFiles.reelsJson());
            System.out.println("igtv: " + exportFiles.igtvJson());
            System.out.println("archived posts: " + exportFiles.archivedPostsJson());

            InstagramProfile profile = instagramProfileImporter.importProfile(exportFiles.personalInformationJson());

            System.out.println("Instagram profile imported:");
            System.out.println("id: " + profile.getId());
            System.out.println("username: " + profile.getUsername());
            System.out.println("display name: " + profile.getDisplayName());
            System.out.println("website: " + profile.getWebsite());
            System.out.println("private account: " + profile.getPrivateAccount());

            ImportResult storiesResult = instagramStoriesImporter.importStories(profile, exportFiles);

            System.out.println("Instagram stories imported:");
            System.out.println("created: " + storiesResult.createdCount());
            System.out.println("updated: " + storiesResult.updatedCount());
            System.out.println("skipped: " + storiesResult.skippedCount());

            ImportResult postsResult = instagramPostsImporter.importPosts(profile, exportFiles);

            System.out.println("Instagram posts imported:");
            System.out.println("created: " + postsResult.createdCount());
            System.out.println("updated: " + postsResult.updatedCount());
            System.out.println("skipped: " + postsResult.skippedCount());

            ImportResult totalResult = storiesResult.plus(postsResult);

            importJob.setCreatedCount(totalResult.createdCount());
            importJob.setUpdatedCount(totalResult.updatedCount());
            importJob.setSkippedCount(totalResult.skippedCount());
            
            importJobService.complete(importJob);

            System.out.println("Import job completed:");
            System.out.println("id: " + importJob.getId());
        } catch (Exception exception) {
            importJobService.fail(importJob, exception);
            throw exception;
        }
    }
}