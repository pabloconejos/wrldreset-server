package com.wrldreset.importer.importer;

public record ImportResult(
        int createdCount,
        int updatedCount,
        int skippedCount
) {

    public static ImportResult empty() {
        return new ImportResult(0, 0, 0);
    }

    public ImportResult plus(ImportResult other) {
        return new ImportResult(
                createdCount + other.createdCount,
                updatedCount + other.updatedCount,
                skippedCount + other.skippedCount
        );
    }
}