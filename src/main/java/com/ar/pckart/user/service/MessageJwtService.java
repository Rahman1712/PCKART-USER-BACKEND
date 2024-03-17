package com.ar.pckart.user.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class MessageJwtService {
	
	@Value("${jwt.secret.key.message}")
	private String SECRET_KEY;

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public String extractOTP(String token) {
		return extractAllClaims(token).get("otp", String.class);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	public String generateTokenForOtp(String email,String otp) {
		return generateToken(new HashMap<>(), email, otp);
	}
	
	public String generateToken(
			Map<String, Object> extractClaims,
			String email, String otp
			) {
		return Jwts
				.builder()
				.setClaims(extractClaims)
				.claim("otp", otp)
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean isTokenValid(String token, String email, String otp) {
		final String username = extractEmail(token);
		final String password = extractOTP(token);
		return (username.equals(email)) &&
				(password.equals(otp)) &&
				!isTokenExpired(token);
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}


}
