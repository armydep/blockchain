package org.am.com.blockchain.wallet.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Apply the security scheme globally
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Authorization");

        return new OpenAPI()
                .info(new Info().title("Swagger API Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
