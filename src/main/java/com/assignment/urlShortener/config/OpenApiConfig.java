package com.assignment.urlShortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("URL Shortener API");

        Info info = new Info()
                .title("URL Shortener API")
                .version("1.0")
                .contact(contact)
                .description("A RESTful API for shortening URLs with custom IDs and TTL support.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
