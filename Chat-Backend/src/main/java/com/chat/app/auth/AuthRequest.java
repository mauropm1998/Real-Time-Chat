package com.chat.app.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {

	@Email(message = "Email mal formatado")
	@NotEmpty(message = "Email é obrigatório")
	@NotBlank(message = "Email é obrigatório")
	private String email;

	@NotEmpty(message = "Palavra-passe é obrigatório")
	@NotBlank(message = "Palavra-passe é obrigatório")
	private String password;

}
