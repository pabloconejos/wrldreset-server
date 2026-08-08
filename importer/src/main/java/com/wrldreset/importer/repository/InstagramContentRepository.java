package com.wrldreset.importer.repository;

import com.wrldreset.importer.entity.InstagramContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// esto busca si ya existe un registro con esa firma
public interface InstagramContentRepository extends JpaRepository<InstagramContent, UUID> {

    Optional<InstagramContent> findByContentSignature(String contentSignature);
}