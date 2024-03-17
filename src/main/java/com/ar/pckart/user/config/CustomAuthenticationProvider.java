package com.ar.pckart.user.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ar.pckart.user.model.User;
import com.ar.pckart.user.repo.UserRepository;
import com.ar.pckart.user.service.UserLoginException;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider   {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private PasswordEncoder encoder;  
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
	    String password = authentication.getCredentials().toString();
		User user = repo.findByUsername(username).orElseThrow(()->
				new UserLoginException("username not found"));
	    if (!encoder.matches(password, user.getPassword())) {
	    	throw new UserLoginException("Invalid Password");
	    }
	    if(!user.isEnabled()) throw new UserLoginException("account is not enabled");
	    if(!user.isNonLocked()) throw new UserLoginException("account is locked");
	    	
	    List<String> userRoles = List.of(user.getRole().name());
	    
	    return new UsernamePasswordAuthenticationToken(username, password, userRoles.stream().map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList()));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}

/*
String username = authentication.getPrincipal() + "";
if (admin == null) throw new BadCredentialsException("Invalid Username");
new UsernameNotFoundException("username not found"));
throw new BadCredentialsException("Invalid Password");

if (admin.isDisabled()) {
    throw new DisabledException("");
    throw new AdminLoginException("account disabled");
}
if (admin.isLocked()) {
    throw new LockedException("");
    throw new AdminLoginException("account locked");
}
 
	    Admin admin = repo.findByUsername(username).get();
 List<Role> userRoles = rolesRepo.getUserRoles(username); // roles from roles table
	    return new UsernamePasswordAuthenticationToken(username, null, userRoles.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList()));
	    return new UsernamePasswordAuthenticationToken(username, null, userRoles.stream().map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList()));
*/