package com.taskmanager.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI configuration for Swagger UI and API documentation.
 * <p>
 * The @SecurityScheme annotation registers the "bearerAuth" scheme globally
 * so Swagger UI shows an Authorize button for JWT input.
 */
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter your JWT token. Obtain one via POST /auth/login with {\"username\":\"admin\",\"password\":\"admin123\"}"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("""
                            Production-quality RESTful Task Management API built with Spring Boot 3.
                            
                            **Authentication:** Most write endpoints require a Bearer JWT token.
                            Obtain a token by calling `POST /auth/login` with credentials:
                            - username: `admin`
                            - password: `admin123`
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Task Manager Team")
                                .email("api@taskmanager.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
