package com.gestordecompras.brisa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Brisa API")
                        .description("API Rest da aplicação Brisa, contendo as funcionalidades de gestão de compras.")
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@brisa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://brisa.com/api/licenca")));
    }
}
