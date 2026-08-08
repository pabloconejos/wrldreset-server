package com.wrldreset.importer.importer;

import com.wrldreset.importer.dto.InstagramPersonalInformationDto;
import com.wrldreset.importer.dto.InstagramProfileUserDto;
import com.wrldreset.importer.dto.InstagramStringValueDto;
import com.wrldreset.importer.entity.InstagramProfile;
import com.wrldreset.importer.repository.InstagramProfileRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Component
public class InstagramProfileImporter {

    private final ObjectMapper objectMapper;
    private final InstagramProfileRepository instagramProfileRepository;

    public InstagramProfileImporter(
            ObjectMapper objectMapper,
            InstagramProfileRepository instagramProfileRepository
    ) {
        this.objectMapper = objectMapper;
        this.instagramProfileRepository = instagramProfileRepository;
    }

    public InstagramProfile importProfile(Path personalInformationJson) throws IOException {
        InstagramPersonalInformationDto personalInformation =
                objectMapper.readValue(personalInformationJson.toFile(), InstagramPersonalInformationDto.class);

        if (personalInformation.profileUser() == null || personalInformation.profileUser().isEmpty()) {
            throw new IllegalStateException("No profile_user found in " + personalInformationJson);
        }

        InstagramProfileUserDto profileUser = personalInformation.profileUser().getFirst();

        Map<String, InstagramStringValueDto> values = profileUser.stringMapData();

        String username = valueOf(values, "Nombre de usuario");
        String displayName = valueOf(values, "Nombre");
        String website = valueOf(values, "Sitio web");
        Boolean privateAccount = booleanValueOf(values, "Cuenta privada");

        if (username == null || username.isBlank()) {
            throw new IllegalStateException("No username found in " + personalInformationJson);
        }

        InstagramProfile profile = instagramProfileRepository.findByUsername(username)
                .orElse(null);

        if (profile == null) {
            profile = new InstagramProfile();
        }

        profile.setUsername(username);
        profile.setDisplayName(displayName);
        profile.setWebsite(website);
        profile.setPrivateAccount(privateAccount);

        return instagramProfileRepository.save(profile);
    }

    private String valueOf(Map<String, InstagramStringValueDto> values, String key) {
        if (values == null) {
            return null;
        }

        InstagramStringValueDto value = values.get(key);

        if (value == null) {
            return null;
        }

        return value.value();
    }

    private Boolean booleanValueOf(Map<String, InstagramStringValueDto> values, String key) {
        String value = valueOf(values, key);

        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.toLowerCase()) {
            case "true", "verdadero", "yes", "si", "sí" -> true;
            case "false", "falso", "no" -> false;
            default -> null;
        };
    }
}