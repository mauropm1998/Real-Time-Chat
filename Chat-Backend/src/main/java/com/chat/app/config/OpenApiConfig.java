package com.chat.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition (
	info = @Info(
		contact = @Contact (
			name ="Mauro Mateus",
			email = "mauro.mateus@selectservices.ao",
			url = "https://localhost:8080"
		),
		description = "Documentação de API do Chat",
		title = "Especificação de API",
		version = "1.0",
		license = @License(
			name = "Minha Licença",
			url = "https://minhalicensa.com"
		),
		termsOfService = "Termos do serviço"
	),
	servers = {
			@Server(
					description = "Local ENV",
					url = "http://localhost:8080/"
			)
	},
	security = {
			@SecurityRequirement(
					name = "bearerAuth"
			)
	}
)

@SecurityScheme(
		name = "bearerAuth",
		description = "JWT auth description",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)

public class OpenApiConfig {

}
