package com.chat.app.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.chat.app.user.User;
import com.chat.app.user.tokens.UserJwtToken;
import com.chat.app.user.tokens.UserJwtTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final UserJwtTokenRepository jwtTokenRepository;

	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${application.security.jwt.secret-key}")
	private String secretKey;

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
		return buildToken(claims, userDetails, jwtExpiration);
	}

	private String buildToken(Map<String, Object> claims, UserDetails userDetails, long jwtExpiration) {
		var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).claim("authorities", authorities)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();

	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public void saveJwtToken(String jwt, User user) {
		var token = UserJwtToken.builder()
				.user(user)
				.token(jwt)
				.expired(false)
				.revoked(false)
				.build();

		jwtTokenRepository.save(token);
	}

	public void revokeAllUserTokens(User user) {
		var userTokens = jwtTokenRepository.findAllValidTokenByUser(user.getId());
		if (!userTokens.isEmpty()) {
			userTokens.forEach(token -> {
				token.setExpired(true);
				token.setRevoked(true);
			});
			jwtTokenRepository.saveAll(userTokens);
		}
	}

}
