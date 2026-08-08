
package com.wrldreset.importer.repository;

import com.wrldreset.importer.entity.InstagramProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstagramProfileRepository extends JpaRepository<InstagramProfile, UUID> {

    Optional<InstagramProfile> findByUsername(String username);
}



/*

Estos metodos los crea automaticamente:
    - save(profile)
    - findById(id)
    - findAll()
    - delete(profile)

 */