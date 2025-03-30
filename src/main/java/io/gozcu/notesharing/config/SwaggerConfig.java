package io.gozcu.notesharing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notesSharingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Note Sharing API")
                        .description("API for a course note sharing platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@notesharing.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ));
    }
}