package com.lniculae.animation_rendered_spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.lniculae.animation_rendered_spring.storage.StorageProperties;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class AnimationRendererSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimationRendererSpringApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

}
