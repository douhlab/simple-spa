package com.dev.simplespa.component.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://brave-sky-0fd134503.6.azurestaticapps.net", "http://localhost:4200")
                        .allowedOrigins("https://www.heyhayhuy.dev")
                        .allowedOrigins("https://heyhayhuy.dev")
                        .allowedMethods("*");
            }
        };
    }
}
