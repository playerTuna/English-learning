package com.example.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example", excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = com.example.config.SecurityConfig.class))
@EntityScan("com.example.entity")
@EnableJpaRepositories("com.example.repository")
@Import(TestSecurityConfig.class)
public class TestApplication {
}