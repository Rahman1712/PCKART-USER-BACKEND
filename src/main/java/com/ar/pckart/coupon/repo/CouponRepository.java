package com.ar.pckart.coupon.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.coupon.model.Coupon;

import jakarta.transaction.Transactional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>{

	@Transactional
	@Modifying
	@Query("UPDATE Coupon c SET "
			+ "c.code = :code, "
			+ "c.discount = :discount, "
			+ "c.validupto = :validupto "
			+ "WHERE c.id = :id")
	void updateCouponByid(@Param("id")Long id, 
			@Param("code") String code,
			@Param("discount") float discount,
			@Param("validupto") LocalDateTime validupto);
	
	@Transactional
	@Modifying
	@Query("UPDATE Coupon c SET "
			+ "c.discount = :discount, "
			+ "c.validupto = :validupto "
			+ "WHERE c.id = :id")
	void updateDiscountAndValidUpto(@Param("id")Long id, 
			@Param("discount") float discount,
			@Param("validupto") LocalDateTime validupto);

	@Modifying
	@Transactional
	@Query("UPDATE Coupon c SET "
			+ "c.enabled = :enabled "
			+ "WHERE c.id = :id")
	void updateEnabledById(@Param("id")Long id, 
			@Param("enabled")boolean enabled);

	Optional<Coupon> findByCode(String code);
}
