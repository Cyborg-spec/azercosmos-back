package com.azercosmos.back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI methaneLeakOpenAPI() {
        Server prodServer = new Server()
                .url("https://azercosmos-back-production.up.railway.app")
                .description("Production Server");

        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");

        Contact contact = new Contact()
                .name("Azercosmos")
                .email("support@azercosmos.az");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Methane Leak Detection API")
                .version("1.0.0")
                .description(
                        "Real-time methane leak monitoring backend with satellite-based AI detection, REST API, and WebSocket notifications.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(prodServer, devServer));
    }
}
