package com.ar.pckart.user.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${jwt.secret.key.users.access_token}")
	private String SECRET_KEY;
	@Value("${jwt.secret.key.users.expiration}")
	private long EXPIRATION; 
	@Value("${jwt.secret.key.users.refresh_token.expiration}")
	private long REFRESH_EXPIRATION; 

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public String extractPassword(String token) {
		return extractAllClaims(token).get("secret", String.class);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}
	private String generateToken(
			Map<String, Object> extractClaims,
			UserDetails userDetails
			) {
		return buildToken(extractClaims, userDetails, EXPIRATION);
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, REFRESH_EXPIRATION);
	}
	
	private String buildToken(
			Map<String, Object> extractClaims,
			UserDetails userDetails,
			long expiration
			) {
		return Jwts
				.builder()
				.setClaims(extractClaims)
				.claim("secret", userDetails.getPassword())
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration)) 
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		final String password = extractPassword(token);
		return (username.equals(userDetails.getUsername())) &&
				(password.equals(userDetails.getPassword())) &&
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
