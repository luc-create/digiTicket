package com.gigiTicket.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
		
		return new OpenAPI().info(info);
	}
}

