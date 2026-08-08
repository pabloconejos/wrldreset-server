package com.wrldreset.importer.importer;

import java.nio.file.Path;

public record InstagramExportFiles(
        Path workingDirectory,
        Path personalInformationJson,
        Path storiesJson,
        Path postsJson,
        Path postsMetadataJson,
        Path reelsJson,
        Path igtvJson,
        Path archivedPostsJson
) {
}