package com.chat.app.auth;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chat.app.security.JwtService;
import com.chat.app.user.User;
import com.chat.app.user.tokens.UserJwtTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserJwtTokenRepository jwtTokenRepository;

	@Transactional
	public AuthResponse authenticate(AuthRequest request) {
		var auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		var user = ((User) auth.getPrincipal());
		return sendJwtToClient(user);
	}

	private AuthResponse sendJwtToClient(User user) {
		var claims = new HashMap<String, Object>();
		claims.put("fullName", user.getUsername());

		var jwtToken = jwtService.generateToken(claims, user);

		jwtService.revokeAllUserTokens(user);
		jwtService.saveJwtToken(jwtToken, user);

		return AuthResponse.builder()
				.id(user.getId())
				.accessToken(jwtToken)
				.fullname(user.getFullname())
				.email(user.getEmail())
				.phone(user.getPhone())				
				.build();
	}

	public CheckTokenResponse checkToken(HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			jwt = authHeader.substring(7);

			var token = jwtTokenRepository.findByToken(jwt).orElse(null);
			if (token != null) {
				var validToken = !token.getExpired() && !token.getRevoked();
				return CheckTokenResponse.builder().response(validToken).build();

			} else {
				return CheckTokenResponse.builder().response(false).build();
			}
		} else {
			return CheckTokenResponse.builder().response(false).build();
		}

	}

	/*
	 * @Transactional
	 * public AuthResponse registration(RegistrationRequest request) {
	 * // Criando Objeto da Empresa
	 * Empresa empresa =
	 * empresaRepository.save(Empresa.builder().estadoLogotipo(EstadoLogotipo.PADRAO
	 * ).build());
	 * 
	 * // Criando Exercicio
	 * anoExercicioRepository.save(AnoExercicio.builder().ano(LocalDate.now().
	 * getYear())
	 * .inicio(LocalDate.of(LocalDate.now().getYear(), 1, 1))
	 * .nome("Exercicio " +
	 * LocalDate.now().getYear()).status(StatusAnoExercicio.ATIVO)
	 * .termino(LocalDate.of(LocalDate.now().getYear(), 12,
	 * 31)).empresa(empresa).build());
	 * 
	 * // Criando Objeto do Usuario e Associando a Empresa
	 * User user = new User();
	 * user.setEmail(request.getEmail());
	 * user.setPassword(passwordEncoder.encode(request.getPassword()));
	 * user.setFullname(request.getFullname());
	 * user.setRoles(Arrays.asList(roleRepository.findByName("ADMIN").get()));
	 * user.setEmpresa(empresa);
	 * user.setTelemovel(request.getPhone());
	 * 
	 * userRepository.save(user);
	 * 
	 * // Retornando a respota de autenticação
	 * return sendJwtToClient(user);
	 * 
	 * }
	 */

}
