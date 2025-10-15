package com.example.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            Map<String, Object> envProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envProperties.put(entry.getKey(), entry.getValue());
            });
            
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", envProperties)
            );
        } catch (Exception e) {
            // Ignore if .env file doesn't exist or can't be read
            System.out.println("Could not load .env file: " + e.getMessage());
        }
    }
}