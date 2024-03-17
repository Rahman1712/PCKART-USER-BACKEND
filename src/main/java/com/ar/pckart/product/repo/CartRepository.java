package com.ar.pckart.product.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.product.dto.CartResponse;
import com.ar.pckart.product.model.Cart;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	
	@Query(value = "SELECT c FROM Cart c "
			+ "INNER JOIN c.user u "
			+ "WHERE u.id = :userId")
	public List<Cart> findAllCartsByUser(@Param("userId") Long userId);

	@Query(value = "SELECT new com.ar.pckart.product.dto.CartResponse("
			+ "c.id,u.id,c.productId,c.quantity) "
			+ "FROM Cart c "
			+ "JOIN c.user u "
			+ "WHERE u.id = :userId")
	public List<CartResponse> findByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT new com.ar.pckart.product.dto.CartResponse("
			+ "c.id,u.id,c.productId,c.quantity) "
			+ "FROM Cart c "
			+ "JOIN c.user u")
	public List<CartResponse> findAllCarts();

	@Modifying
	@Transactional
	@Query("update Cart c set c.quantity = :quantity where c.id = :id")
	public void updateQuantity(@Param("id") Long id, @Param("quantity") int quantity);

	@Query(value = "SELECT c FROM Cart c WHERE c.user.id = :userId AND c.productId = :productId")
	public Optional<Cart> findByUserAndProductId(@Param("userId") Long userId,@Param("productId") Long productId); 

	@Modifying
	@Transactional
	@Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.productId = :productId")
	public void deleteCartItemByUserIdAndProductId(
			@Param("userId") Long userId, @Param("productId") Long productId);
}
