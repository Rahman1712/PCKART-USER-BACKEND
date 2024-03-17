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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.product.dto.CartResponse;
import com.ar.pckart.product.model.Cart;
import com.ar.pckart.product.service.CartService;

@RestController
@RequestMapping("/pckart/api/v1/cart")
public class CartController {

	@Autowired private CartService cartService;

	@PostMapping("/add")
	public ResponseEntity<CartResponse> addToCart(@RequestBody Cart cart) {
		return ResponseEntity.ok(cartService.save(cart));
	}
	
	@GetMapping("/get/all-carts")
	public ResponseEntity<List<CartResponse>> allCartsList(){
		return ResponseEntity.ok(cartService.cartList());
	}
	
	@GetMapping("/get/carts/{userid}")
	public ResponseEntity<?> getCartItemsByUserId(@PathVariable("userid") Long userid) {
		List<CartResponse> cartRes = cartService.cartItemsByUserId(userid);
		return ResponseEntity.status(HttpStatus.OK).body(cartRes);	
	}
	
	@PutMapping("/update-cart-quantity/{id}")
	public ResponseEntity<?> updateCartQuantiy(@PathVariable("id") Long id,
			@RequestParam("quantity") int quantity) {
		System.err.println(id+"  "+quantity);
		cartService.update(id, quantity);
		return ResponseEntity.ok("Cart quantity update");
	}
	
	@DeleteMapping("/delete-cart/{id}")
	public ResponseEntity<String> deleteCartById(@PathVariable("id") Long id) {
		cartService.deleteCart(id);
		return ResponseEntity.ok("Cart deleted");
	}
	
	@PutMapping("/getAndUpdate/{userid}")
	public ResponseEntity<List<CartResponse>> getAndUpdate(
			@PathVariable("userid") Long userid, 
			@RequestBody List<Cart> carts
			){
		System.err.println("RRRRRRRRR");
		System.err.println(carts);
		System.err.println("WWWWWWWWWW");
		List<CartResponse> updateCarts = cartService.getAndUpdate(userid,carts);
		System.err.println("OOOOOOOO");
//		System.err.println(updateCarts);
		
		return ResponseEntity.ok(updateCarts);
	}
	

}



//@PutMapping("/getAndUpdate/{userid}")
//public ResponseEntity<List<Cart>> getAndUpdate(
//		@PathVariable("userid") Long userid, 
//		@RequestBody List<Cart> carts
//		){
//	System.err.println("RRRRRRRRR");
//	System.err.println(carts);
//	System.err.println("WWWWWWWWWW");
////	List<Cart> updateCarts = cartService.getAndUpdate(userid,carts);
//	System.err.println("OOOOOOOO");
////	System.err.println(updateCarts);
//	
////	return ResponseEntity.ok(updateCarts);
//	return ResponseEntity.ok(carts);
//}
