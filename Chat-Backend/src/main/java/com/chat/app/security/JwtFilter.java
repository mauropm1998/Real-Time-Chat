package com.chat.app.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chat.app.user.UserRepository;
import com.chat.app.user.tokens.UserJwtTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserJwtTokenRepository jwtTokenRepository;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		final String userEmail;

		if (request.getServletPath().contains("/api/v1/auth")) {
			filterChain.doFilter(request, response);
		}

		else if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
		}

		else {
			jwt = authHeader.substring(7);
			userEmail = jwtService.extractUsername(jwt);

			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
				var token = jwtTokenRepository.findByToken(jwt).orElse(null);

				if (jwtService.isTokenValid(jwt, userDetails) && !token.getExpired() && !token.getRevoked()) {
					AutenticarDiretamente(userDetails, request);

					// Atualiza o último visto do usuário
					var user = userRepository.findByEmail(userEmail).orElse(null);
					if (user != null) {
						user.setLastSeen(LocalDateTime.now());
						userRepository.save(user);
					}
				}
			}

			filterChain.doFilter(request, response);
		}
	}

	private void AutenticarDiretamente(UserDetails userDetails, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities());

		userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(userToken);
	}

}
