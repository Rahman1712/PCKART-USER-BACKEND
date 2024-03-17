package com.ar.pckart.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.user.dto.UserDTO;
import com.ar.pckart.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pckart/api/v1/user-to-admin/user")
@RequiredArgsConstructor
public class AdminController {
	
	private final UserService userService;

	@GetMapping("/get/allUsers")
	public ResponseEntity<List<UserDTO>> getDetailsOfAllUsers(){
		return ResponseEntity.ok(userService.allUsersDtos());
	}
	
	@GetMapping("/getbyid/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable("id")Long id){
		return ResponseEntity.ok(userService.findUserById(id));
	}
	
	@PutMapping("/update/nonlocked/byid/{userId}")
	public ResponseEntity<String> updateById(@PathVariable("userId")Long userId,
			@RequestParam("nonlocked") boolean nonlocked){
		return ResponseEntity.ok(userService.updateNonLocked(userId, nonlocked));
	}
}
