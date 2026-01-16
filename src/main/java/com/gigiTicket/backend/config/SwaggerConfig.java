package com.gigiTicket.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		Contact contact = new Contact();
		contact.setName("GigiTicket Team");
		
		Info info = new Info();
		info.setTitle("GigiTicket API");
		info.setVersion("1.0.0");
		info.setDescription("API de gestion de tickets (Helpdesk)");
		info.setContact(contact);
		
		return new OpenAPI()
				.info(info)
				.components(new Components()
						.addSecuritySchemes("bearerAuth", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("Entrez votre token JWT (obtenu via /api/auth/login)")));
	}
}

