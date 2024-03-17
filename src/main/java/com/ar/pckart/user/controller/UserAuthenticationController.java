package com.ar.pckart.user.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.user.dto.AuthenticationRequest;
import com.ar.pckart.user.dto.AuthenticationResponse;
import com.ar.pckart.user.dto.UserRegisterRequest;
import com.ar.pckart.user.service.UserService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserAuthenticationController {

	private final UserService userService;
	
	@GetMapping("/work")
	public String work() {
		return "Worked";
	}

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserRegisterRequest request) {
		return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(userService.authenticate(request));
	}
	
	@PostMapping("/refresh-token")
	public void refresh(
		HttpServletRequest request,
		HttpServletResponse response
	) throws StreamWriteException, DatabindException, IOException {
		
		userService.refreshToken(request, response);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<?> verifyToken(@RequestParam String token) {
		return ResponseEntity.ok(userService.verifyOTP(token));
	}
	
	@PostMapping("/forgot-password/{email}")
	public ResponseEntity<?> resetToken(@PathVariable("email") String email) throws UnsupportedEncodingException, MessagingException {
//		return ResponseEntity.ok("yse kk");
		return ResponseEntity.ok(userService.resetPasswordRequest(email));
	}
	
	@PutMapping("/update/forgot-password/byemail/{email}")
	public ResponseEntity<String> updateForgotPasswordByEmail(
			@PathVariable("email") String email,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("otp") String otp
			){
		
		return ResponseEntity.ok(userService.updateForgotPasswordByEmail(email,newPassword,otp));
	}
	

	@PostMapping("/check")
	public ResponseEntity<AuthenticationResponse> authenticate(HttpServletRequest req, HttpServletResponse res) {
		String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null) {
			res.setStatus(401);
			return ResponseEntity.badRequest().build();
		}
		String token = authHeader.split(" ")[1];
		if (token == null) {
			res.setStatus(401);
			return ResponseEntity.badRequest().build();
		}
		AuthenticationResponse response =  new AuthenticationResponse();
		response.setAccessToken(token);

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/message")
	public ResponseEntity<String> message(@RequestParam String email) {
		try {
			return ResponseEntity.ok(userService.sendMailForVerify(email));  
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error : Email Not Send ");
		}
	}
	

}

/*
 * @PostMapping("/register") public ResponseEntity<AuthenticationResponse>
 * register( //public ResponseEntity<Object> register(
 * 
 * @RequestBody @Valid RegisterRequest request //,BindingResult bindingResult ){
 * //if(bindingResult.hasErrors()) //return
 * ResponseEntity.badRequest().body(bindingResult.getFieldError()); //return
 * ResponseEntity.badRequest().build();
 * 
 * //return ResponseEntity.ok(service.register(request)); return new
 * ResponseEntity<>(service.register(request), HttpStatus.CREATED); }
 */
