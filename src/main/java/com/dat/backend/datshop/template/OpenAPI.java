package com.dat.backend.datshop.template;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "DatShop", version = "v1.0.0", description = "Rest api for my E-commerce project"),
        servers = {
                @Server(
                        description = "Local env",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Public",
                        url = "https://public"
                )
        },
        security = @SecurityRequirement(
                name = "BearerAuth"
        )
)
@SecurityScheme(
        name = "BearerAuth",
        description = "Enter JWT token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPI {
}
