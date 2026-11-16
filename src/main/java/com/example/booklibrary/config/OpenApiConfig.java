package com.example.booklibrary.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Book Library REST API")
                        .version("1.0.0")
                        .description("Backend REST API demonstrating layered architecture, Spring Data JPA, " +
                                "relationships (@ManyToOne/@OneToMany, @ManyToMany), JSR-303 validation, " +
                                "pagination/sorting, and multipart file upload.")
                        .contact(new Contact()
                                .name("Student Project")
                                .email("student@university.edu"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io")));
    }
}
