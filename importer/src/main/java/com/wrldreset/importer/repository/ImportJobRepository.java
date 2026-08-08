package com.wrldreset.importer.repository;

import com.wrldreset.importer.entity.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportJobRepository extends JpaRepository<ImportJob, UUID> {
}