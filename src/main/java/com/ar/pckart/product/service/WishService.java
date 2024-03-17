package com.ar.pckart.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ar.pckart.product.dto.WishResponse;
import com.ar.pckart.product.model.Wish;
import com.ar.pckart.product.repo.WishRepository;

@Service
public class WishService {

	@Autowired private WishRepository wishRepository;

	public WishResponse save(Wish wish) {
		Optional<Wish> findByProductId = wishRepository.findByProductId(wish.getProductId());
		if(!findByProductId.isPresent()) {
			Wish wishSaved = wishRepository.save(wish);
			return WishResponse.builder()
					.wishId(wishSaved.getId())
					.userId(wishSaved.getUser().getId())
					.productId(wishSaved.getProductId())
					.build();
		}
		return WishResponse.builder()
			.wishId(wish.getId())
			.userId(wish.getUser().getId())
			.productId(wish.getProductId())
			.build();
	}
	
	public Optional<Wish> findWishById(Long wishId) {
		return wishRepository.findById(wishId);
	}

	public List<WishResponse> wishsList() {
		return wishRepository.findAllWish();
	}

	public List<WishResponse> wishItemsByUserId(Long userId) {
		return wishRepository.findAllWishByUserId(userId);
	}

	public void deleteWish(Long cartId) {
		wishRepository.deleteById(cartId);
	}
	
	public List<WishResponse> getAndUpdate(Long userId, List<Wish> wishs){

		List<Wish> findAllWishsByUser = wishRepository.findAllWishsByUser(userId);
		
		if(findAllWishsByUser.isEmpty() && wishs.isEmpty()) return new ArrayList<WishResponse>(); //return null;
		
		if(!findAllWishsByUser.isEmpty()) {
			wishs.forEach(cacheWish -> {
				// Check if the product ID exists in the 'carts' list
				boolean found = false;
				for (Wish repoWish : findAllWishsByUser) {
					if (cacheWish.getProductId().equals(repoWish.getProductId())) {
						found = true;
						break;
					}
				}
				
				if (!found) {
		            // Add the cache wish to the repository
					wishRepository.save(cacheWish);
				}
			});
		}else if(!wishs.isEmpty()) {
			for (Wish wish : wishs) {
				wishRepository.save(wish);
			}
		}

		return wishRepository.findAllWishByUserId(userId);
	}
	
}
