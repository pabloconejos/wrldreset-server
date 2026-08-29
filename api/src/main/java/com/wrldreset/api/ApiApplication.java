package com.wrldreset.api;
import com.wrldreset.api.config.WrldresetStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(WrldresetStorageProperties.class)
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
