package org.example.backend_farmacia.config;

import lombok.RequiredArgsConstructor;
import org.example.backend_farmacia.service.ImageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ImageService imageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = imageService.getUploadDir().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)));
    }
}
