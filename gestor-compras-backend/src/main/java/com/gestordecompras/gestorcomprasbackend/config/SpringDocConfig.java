package com.gestordecompras.gestorcomprasbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Gestor Compras Backend API")
                        .description("API Rest da aplicação Gestor Compras Backend, contendo as funcionalidades de gestão de compras.")
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@gestorcomprasbackend.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://gestorcomprasbackend.com/api/licenca")));
    }
}
