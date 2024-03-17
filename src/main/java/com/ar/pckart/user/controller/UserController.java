package com.ar.pckart.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ar.pckart.user.dto.UserRegisterRequest;
import com.ar.pckart.user.model.Address;
import com.ar.pckart.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/get/by/id/{id}")
	public ResponseEntity<?> getByUserId(
			@PathVariable("id")Long id){
		return ResponseEntity.ok(userService.findUserById(id));
	}
	@GetMapping("/get/by/username/{username}")
	public ResponseEntity<?> getByUsername(
			@PathVariable("username")String username){
		return ResponseEntity.ok(userService.findByUsername(username));
	}
	@GetMapping("/get/by/email/{email}")
	public ResponseEntity<?> getByEmail(
			@PathVariable("email")String email){
		return ResponseEntity.ok(userService.findUserByEmail(email));
	}
	
	@PutMapping("/update/byId/{id}")
	public ResponseEntity<String> updateById(@PathVariable("id")Long id,
			@RequestPart("request") UserRegisterRequest request,
			@RequestParam("file") MultipartFile file){
		try {
			return ResponseEntity.ok(userService.updateUserById(id, file, request));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Updation Failed");
		}
	}
	@PutMapping("/update/byUsername/{username}")
	public ResponseEntity<String> updateByUsername(@PathVariable("username")String username,
			@RequestPart("request") UserRegisterRequest request,
			@RequestParam("file") MultipartFile file){
		try {
			Long id = userService.findIdByUsername(username);
			return ResponseEntity.ok(userService.updateUserById(id, file, request));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Updation Failed");
		}
	}
	
	@PutMapping("/update/password/byId/{id}")
	public ResponseEntity<String> updatePasswordById(@PathVariable("id")Long id,
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword
			){
		return ResponseEntity.ok(userService.updatePasswordById(id,currentPassword,newPassword));
	}
//	@PutMapping("/update/password/byusername/{username}")
//	public ResponseEntity<String> updatePasswordByUsername(@PathVariable("username")String username,
//			@RequestParam("currentPassword") String currentPassword,
//			@RequestParam("newPassword") String newPassword
//			){
//		Long id = userService.findIdByUsername(username);
//		return ResponseEntity.ok(userService.updatePasswordById(id,currentPassword,newPassword));
//	}

	@PutMapping("/update/addresses/{username}")
	public ResponseEntity<?> address(@PathVariable("username") String username,
			@RequestBody List<Address> addresses) {
		Long id = userService.findIdByUsername(username);
		return ResponseEntity.ok(userService.address(id,addresses));
	}
	
	@PutMapping("/update/address/contact-only/{id}")
	public ResponseEntity<?> updateAddressContactOnly(
			@PathVariable("id") Long id,
			@RequestParam("contact") String contact
			) {
		return ResponseEntity.ok(userService.updateAddressContactOnly(id,contact));
	}
	
	@PutMapping("/update/address/contacts/{id}")
	public ResponseEntity<?> updateAddressContact(
			@PathVariable("id") Long id,
			@RequestParam("contact") String contact,
			@RequestParam("alternative_contact") String alternative_contact
			) {
		return ResponseEntity.ok(userService.updateAddressContact(id,contact,alternative_contact));
	}
	@PutMapping("/update/address/selected/{id}")
	public ResponseEntity<?> updateAddressSelected(
			@PathVariable("id") Long id,
	        @RequestParam("userId") Long userId
			) {
		userService.updateAddressSeleted(id, userId);
		return ResponseEntity.ok("YES");
	}
	
	@DeleteMapping("/delete/address/{id}")
	public ResponseEntity<?> deleteAddress(
			@PathVariable("id") Long id) {
		return ResponseEntity.ok(userService.deleteAddress(id));
	}

	@GetMapping("/get/all/addresses/byid/{id}")
	public ResponseEntity<List<Address>> allAdressesById(@PathVariable("id") Long id){
		return ResponseEntity.ok(userService.getAddressesByUserId(id));
	}
	@GetMapping("/get/all/addresses/byusername/{username}")
	public ResponseEntity<List<Address>> allAdressesByUsername(@PathVariable("username") String username){
		Long id = userService.findIdByUsername(username);
		return ResponseEntity.ok(userService.getAddressesByUserId(id));
	}
	
	@DeleteMapping("/delete/byid/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id){
		return ResponseEntity.ok(userService.deleteUserById(id));
	}
	
//	@PutMapping("/update/add{id}")
//	public ResponseEntity<String> updateA(@PathVariable Long id, @RequestParam Long adid){
//		return ResponseEntity.ok(userService.updateA(id,adid));
//	} 
//	@DeleteMapping("da/{adid}")
//	public ResponseEntity<String> deleteanan(@PathVariable Long adid){
//		return ResponseEntity.ok(userService.deleteAddress(adid));
//	}
}
