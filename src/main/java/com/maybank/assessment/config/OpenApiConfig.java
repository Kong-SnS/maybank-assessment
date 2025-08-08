package com.maybank.assessment.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
                        .title("Transaction API")
                        .description("Maybank Technical Test - Transaction Management API")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Fook Shen")
                                .email("fookshen12@gmail.com")
                                .url("https://github.com/Kong-SnS"))
                        .license(new License().name("MIT License")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/Kong-SnS/maybank-assessment"));
    }
}

