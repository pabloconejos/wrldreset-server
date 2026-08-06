package com.wrldreset.importer.config;

import com.wrldreset.importer.importer.InstagramZipFinder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupConfigLogger implements CommandLineRunner {

    private final WrldresetStorageProperties storageProperties;
    private final InstagramZipFinder instagramZipFinder;

    public StartupConfigLogger(
            WrldresetStorageProperties storageProperties,
            InstagramZipFinder instagramZipFinder
    ) {
        this.storageProperties = storageProperties;
        this.instagramZipFinder = instagramZipFinder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("wrldReset importer storage paths:");
        System.out.println("imports: " + storageProperties.getImportsPath());
        System.out.println("media: " + storageProperties.getMediaPath());
        System.out.println("temp: " + storageProperties.getTempPath());

        System.out.println("Instagram ZIP files:");
        instagramZipFinder.findZipFiles()
                .forEach(path -> System.out.println("- " + path));
    }
}