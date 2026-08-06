package com.wrldreset.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.wrldreset.importer.config.WrldresetStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WrldresetStorageProperties.class)
public class WrldresetImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(WrldresetImporterApplication.class, args);
	}

}
