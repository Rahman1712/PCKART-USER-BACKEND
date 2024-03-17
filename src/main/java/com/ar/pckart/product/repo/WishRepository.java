package com.ar.pckart.product.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.product.dto.WishResponse;
import com.ar.pckart.product.model.Wish;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long>{
	
	@Query(value = "SELECT w FROM Wish w "
			+ "INNER JOIN w.user u "
			+ "WHERE w.id = :userId")
	public List<Wish> findAllWishsByUser(@Param("userId") Long userId);

	@Query(value = "SELECT new com.ar.pckart.product.dto.WishResponse("
			+ "w.id,u.id,w.productId) "
			+ "FROM Wish w "
			+ "JOIN w.user u "
			+ "WHERE u.id = :userId")
	public List<WishResponse> findAllWishByUserId(@Param("userId") Long userId);

	@Query(value = "SELECT new com.ar.pckart.product.dto.WishResponse("
			+ "w.id,u.id,w.productId) "
			+ "FROM Wish w "
			+ "JOIN w.user u")
	public List<WishResponse> findAllWish();

	@Query(value = "SELECT w FROM Wish w WHERE w.user.id = :userId AND w.productId = :productId")
	public Optional<Wish> findByUserAndProductId(@Param("userId") Long userId,@Param("productId") Long productId);

	public Optional<Wish> findByProductId(Long productId); 

}
