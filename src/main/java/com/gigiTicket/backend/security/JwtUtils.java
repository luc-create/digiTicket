package com.gigiTicket.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

	@Value("${jwt.secret:maCleSecreteTresLongueEtSecuriseePourLeProjetDigiTicket2024}")
	private String jwtSecret;

	@Value("${jwt.expiration:86400000}")
	private int jwtExpirationMs;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateJwtToken(String email) {
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();
	}

	public String getEmailFromJwtToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}

