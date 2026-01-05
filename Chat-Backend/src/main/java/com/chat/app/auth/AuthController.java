package com.chat.app.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

	private final AuthService service;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponse> authenticate(@RequestBody @Valid AuthRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

	@GetMapping("/check-token")
	public ResponseEntity<CheckTokenResponse> checkToken(HttpServletRequest request) {
		return ResponseEntity.ok(service.checkToken(request));
	}
	/*
	 * @PostMapping("/registration")
	 * public ResponseEntity<AuthResponse> registration(@RequestBody @Valid
	 * RegistrationRequest request) {
	 * return ResponseEntity.ok(service.registration(request));
	 * }
	 */

}
