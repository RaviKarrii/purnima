package com.example.purnima.config;

import com.example.purnima.PurnimaAstrology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PurnimaAstrology purnimaAstrology() {
        // Use accurate calculations (Swiss Ephemeris) by default
        return new PurnimaAstrology(true);
    }
}
