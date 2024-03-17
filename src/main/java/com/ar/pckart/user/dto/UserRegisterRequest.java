package com.ar.pckart.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

	@Size(min = 2, max = 50, message = "invalid firstname")
	private String fullname;

	@Email(message = "Invalid email id")
	private String email;

	@Pattern(regexp = "^\\d{10}$", message = "Invalid Mobile Number")
	private String mobile;

	@NotBlank
	@Size(min = 5, max = 15)
	private String username;

	@NotBlank
	@Size(min = 5, max = 15)
	private String password;

	
}

//	@Min(18)
//	@Max(60)
//	private int age;