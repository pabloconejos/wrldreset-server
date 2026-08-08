package com.wrldreset.importer.importer;

import com.wrldreset.importer.entity.ImportJob;
import com.wrldreset.importer.entity.ImportJobStatus;
import com.wrldreset.importer.repository.ImportJobRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ImportJobService {

    private final ImportJobRepository importJobRepository;

    public ImportJobService(ImportJobRepository importJobRepository) {
        this.importJobRepository = importJobRepository;
    }

    public ImportJob start() {
        ImportJob importJob = new ImportJob();
        importJob.setStatus(ImportJobStatus.RUNNING);
        importJob.setStartedAt(Instant.now());
        importJob.setCreatedCount(0);
        importJob.setUpdatedCount(0);
        importJob.setSkippedCount(0);

        return importJobRepository.save(importJob);
    }

    public ImportJob complete(ImportJob importJob) {
        importJob.setStatus(ImportJobStatus.COMPLETED);
        importJob.setCompletedAt(Instant.now());

        return importJobRepository.save(importJob);
    }

    public ImportJob fail(ImportJob importJob, Exception exception) {
        importJob.setStatus(ImportJobStatus.FAILED);
        importJob.setCompletedAt(Instant.now());
        importJob.setErrorMessage(exception.getMessage());

        return importJobRepository.save(importJob);
    }
}