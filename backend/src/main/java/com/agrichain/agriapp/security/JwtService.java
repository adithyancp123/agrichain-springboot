package com.agrichain.agriapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

	private final Key signingKey;
	private final long expirationMs;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expirationMs:3600000}") long expirationMs
	) {
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(String username, String role) {
		long now = System.currentTimeMillis();

		return Jwts.builder()
				.setSubject(username)
				.claim("role", role)
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + expirationMs))
				.signWith(signingKey)
				.compact();
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public String extractRole(String token) {
		Object role = parseClaims(token).get("role");
		return role != null ? role.toString() : null;
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.setSigningKey(signingKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
}

