package com.ssafy.hm.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    private String uploadPath;

    @PostConstruct
    public void ensureUploadDirectory() {
        Path path = Paths.get(uploadPath);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create upload directory: " + uploadPath, e);
        }
    }

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
			.allowCredentials(false);
	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploaded/**")
            .addResourceLocations("classpath:/static/uploaded/", "file:///" + uploadPath);
    }
}
