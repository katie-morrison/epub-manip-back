package com.katiemorrison.epub_manip_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer{
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST")
            .allowedHeaders("*")
            .allowCredentials(true);

        registry.addMapping("/calculateDiagnostics")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("POST")
            .allowedHeaders("*")
            .allowCredentials(true);

        registry.addMapping("/getEpub/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
