package com.wrldreset.importer.importer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InstagramImportRunner implements CommandLineRunner {

    private final InstagramImportWorkflow instagramImportWorkflow;

    public InstagramImportRunner(InstagramImportWorkflow instagramImportWorkflow) {
        this.instagramImportWorkflow = instagramImportWorkflow;
    }

    @Override
    public void run(String... args) throws Exception {
        instagramImportWorkflow.importFromConfiguredImportsFolder();
    }
}