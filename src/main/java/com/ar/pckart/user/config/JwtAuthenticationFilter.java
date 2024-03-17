package com.ar.pckart.user.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ar.pckart.user.repo.TokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService; 
	private final TokenRepository tokenRepository;
	
	@Value("${user.request.uri}")
	private String USER_REQUEST_URI;
	
	@Value("${jwt.filter.disable.request.uris}")
	private String[] JWT_FILTER_DISABLE_REQUEST_URIS;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException { 
		
		if(request.getRequestURI().startsWith(USER_REQUEST_URI)){
			filterChain.doFilter(request, response);
			return;
		}
		

		for(String uri: JWT_FILTER_DISABLE_REQUEST_URIS) {
			String uriPath = uri.contains("/**") ? uri.replace("/**", "") 
					: (uri.contains("/*") ? uri.replace("/*", "") : uri);
			
			System.err.println("URIPATH :: " + uriPath);
			if(request.getRequestURI().startsWith(uriPath)){
				System.err.println("USSSSSSSURIIIIIIIIIII FILTER KIN");
				filterChain.doFilter(request, response);
				return; 
			}
		}

		System.err.println("URIPATH OUTSIDE in to jwt.............");
		
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt;
		final String userName;
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		//jwt = authHeader.substring(7);
		jwt = authHeader.split(" ")[1].trim();
		userName = jwtService.extractUsername(jwt);
		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
			var isTokenValid = tokenRepository.findByToken(jwt)
					.map(t -> !t.isExpired() && !t.isRevoked())
					.orElse(false);
			
			if(jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
				UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
				);
				authToken.setDetails(
						new WebAuthenticationDetailsSource()
							.buildDetails(request)
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
