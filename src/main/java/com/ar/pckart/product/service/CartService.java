package com.ar.pckart.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ar.pckart.product.dto.CartResponse;
import com.ar.pckart.product.model.Cart;
import com.ar.pckart.product.repo.CartRepository;

@Service
public class CartService {
	
	@Autowired private CartRepository cartRepository;
	@Autowired private ProductService productService;

	public CartResponse save(Cart cart) {
		Cart cartSaved = cartRepository.save(cart);
		return CartResponse.builder()
			.cartId(cartSaved.getId())
			.userId(cartSaved.getUser().getId())
			.productId(cartSaved.getProductId())
			.quantity(cartSaved.getQuantity())
			.build(); 
	}
	
	public Optional<Cart> findByUserIdAndProductId(Long userId, Long productId) {
		return cartRepository.findByUserAndProductId(userId,productId);
	}
	
	public void update(Long cartId, int quantity) {
		cartRepository.updateQuantity(cartId, quantity);
	}

	public List<CartResponse> cartList() {
		return cartRepository.findAllCarts();
	}

	public List<CartResponse> cartItemsByUserId(Long userId) {
		return cartRepository.findByUserId(userId);
	}

	public void deleteCart(Long cartId) {
		cartRepository.deleteById(cartId);
	}
	
	public String deleteCartsByUserIdAndProductId(Long userId, List<Long> productIdList) {
		for(Long productId: productIdList) {
			cartRepository.deleteCartItemByUserIdAndProductId(userId, productId);
		}
		return "cartItems deleted :"+productIdList.size()+" carts deleted";
	}
	
	
	public List<CartResponse> getAndUpdate(Long userId, List<Cart> carts) {
		List<Cart> findAllCartsByUser = cartRepository.findAllCartsByUser(userId);
		
		if(findAllCartsByUser.isEmpty() && carts.isEmpty()) return new ArrayList<CartResponse>(); //return null;
		
		if(!findAllCartsByUser.isEmpty()) {
			carts.forEach(cacheCart -> {
				boolean found = false;
				Cart newCart = cacheCart; // this for already in repo cart, just initialize to cachecart
				for (Cart repoCart : findAllCartsByUser) {
					if (repoCart.getProductId().equals(cacheCart.getProductId())) {
						Integer productQuantity = productService.getProductById(cacheCart.getProductId());
						// Update the existing cart
						if((repoCart.getQuantity()+cacheCart.getQuantity()) > productQuantity) {
							repoCart.setQuantity(productQuantity);
						}else {
							repoCart.setQuantity(repoCart.getQuantity()+cacheCart.getQuantity());
						}
						newCart = repoCart;
						found = true;
						break;
					}
				}
				
				if (!found) {
	            	// Add the new cart to the repository
	            	cartRepository.save(cacheCart);
	        	}else {
	            	// Save the updated cart
	            	cartRepository.save(newCart);
	        	}
			});
		}else if(!carts.isEmpty()) {
			for (Cart cart : carts) {
				// Update the existing cart
				Integer productQuantity = productService.getProductById(cart.getProductId());
				if(cart.getQuantity() > productQuantity) {
					cart.setQuantity(productQuantity);
				}
				cartRepository.save(cart);
			}
		}
		
		//return cartRepository.findAllCartsByUser(userId);
		return cartRepository.findByUserId(userId); 
	}
}
