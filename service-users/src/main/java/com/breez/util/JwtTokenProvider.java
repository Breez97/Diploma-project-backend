package com.breez.util;

import com.breez.exception.auth.JwtAuthenticationException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.access.token.expiration.ms}")
	private Long accessExpirationMs;

	@Value("${jwt.refresh.token.expiration.ms}")
	private Long refreshExpirationMs;

	private final SecretKey secretKey;

	public String generateAccessToken(Authentication authentication) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		return generateAccessToken(userPrincipal.getUsername());
	}

	public String generateAccessToken(String email) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, email, accessExpirationMs);
	}

	public String generateRefreshToken(Authentication authentication) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
		return generateRefreshToken(userPrincipal.getUsername());
	}

	public String generateRefreshToken(String email) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, email, refreshExpirationMs);
	}

	private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		LocalDateTime validity = now.plusNanos(expirationMs * 1_000_000L);
		Date issuedAtDate = Date.from(now.toInstant(ZoneOffset.UTC));
		Date expirationDate = Date.from(validity.toInstant(ZoneOffset.UTC));

		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(issuedAtDate)
				.expiration(expirationDate)
				.signWith(secretKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public String getEmailFromToken(String token) {
		return getClaimsFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimsFromToken(token, Claims::getExpiration);
	}

	public LocalDateTime getExpirationLocalDateTimeFromToken(String token) {
		Date expirationDate = getExpirationDateFromToken(token);
		return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneOffset.UTC);
	}

	public LocalDateTime getIssuedAtLocalDateTimeFromToken(String token) {
		Date issuedAtDate = getClaimsFromToken(token, Claims::getIssuedAt);
		return LocalDateTime.ofInstant(issuedAtDate.toInstant(), ZoneOffset.UTC);
	}

	public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (Exception e) {
			logger.warn("JWT token compact of handler are invalid: {}", e.getMessage());
			throw new JwtAuthenticationException("Invalid JWT");
		}
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			logger.error("Invalid JWT token");
		}
		return false;
	}

	private boolean isTokenExpired(String token) {
		try {
			return getExpirationDateFromToken(token).before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			logger.error("Cannot check expiration on invalid JWT token: {}", e.getMessage());
			return true;
		}
	}

	public LocalDateTime getRefreshExpirationLocalDateTime() {
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		return now.plusNanos(refreshExpirationMs * 1_000_000L);
	}
}
