package com.chat.app.security;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class Security {

	private final AuthenticationProvider authProvider;
	private final JwtFilter jwtAuthFilter;
	private final LogoutHandlerImpl handlerImpl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(req -> req.requestMatchers(
						"/",
						"/chat/**",
						"/public/**",
						"/file.png",
						"/avatar.png",
						"/index.html",
						"/images/**",
						"/ringtones/**",
						"/ws/**",
						"/favicon.ico",
						"/assets/**",
						"/css/**",
						"/fonts/**",
						"/js/**",
						"/ws/**",
						"/api/v1/auth/**",
						"/v2/api-docs",
						"/v3/api-docs",
						"/v3/api-docs/**",
						"/swagger-resources",
						"/swagger-resources/**",
						"/configuration/ui",
						"/configuration/ui/security",
						"/swagger-ui/**",
						"/webjars/**",
						"/swagger-ui.html").permitAll()
						.anyRequest().authenticated())
				.authenticationProvider(authProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
						.logoutUrl("/api/v1/auth/logout")
						.addLogoutHandler(handlerImpl)
						.logoutSuccessHandler((request, response, auth) -> {
							SecurityContextHolder.clearContext();
						}))
				// Configuração para evitar redirecionamento automático
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint((request, response, authException) -> {
							// Envia 403 Forbidden em vez de redirecionar para OAuth2
							response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
						}))

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
