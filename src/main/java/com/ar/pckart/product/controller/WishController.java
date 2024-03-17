package com.ar.pckart.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.product.dto.WishResponse;
import com.ar.pckart.product.model.Wish;
import com.ar.pckart.product.service.WishService;

@RestController
@RequestMapping("/pckart/api/v1/wish")
public class WishController {


	@Autowired private WishService wishService;

	@PostMapping("/add")
	public ResponseEntity<WishResponse> addToWish(@RequestBody Wish wish) {
		return ResponseEntity.ok(wishService.save(wish));
	}
	
	@GetMapping("/get/all-wish")
	public ResponseEntity<List<WishResponse>> allWishsList(){
		return ResponseEntity.ok(wishService.wishsList());
	}
	
	@GetMapping("/get/wishs/{userid}")
	public ResponseEntity<?> getWishsItemsByUserId(@PathVariable("userid") Long userid) {
		List<WishResponse> wishRes = wishService.wishItemsByUserId(userid);
		return ResponseEntity.status(HttpStatus.OK).body(wishRes);	
	}
	
	@DeleteMapping("/delete-wish/{id}")
	public ResponseEntity<String> deleteWishById(@PathVariable("id") Long id) {
		wishService.deleteWish(id);
		return ResponseEntity.ok("Wish deleted");
	}
	
	@PutMapping("/getAndUpdate/{userid}")
	public ResponseEntity<List<WishResponse>> getAndUpdate(
			@PathVariable("userid") Long userid,
			@RequestBody List<Wish> wishs){
		List<WishResponse> updateWishs = wishService.getAndUpdate(userid,wishs);
		return ResponseEntity.ok(updateWishs);
	}

}
