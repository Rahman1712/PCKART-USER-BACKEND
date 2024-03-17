package com.ar.pckart.coupon.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.coupon.model.UserCoupon;

import jakarta.transaction.Transactional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>{

	@Modifying
	@Transactional
	@Query("UPDATE UserCoupon c SET "
			+ "c.coupon_used = :coupon_used,"
			+ "c.used_date = :used_date "
			+ "WHERE c.id = :id")
	public void updateCouponUsedAndUsedDateById(@Param("id")Long id, 
			@Param("coupon_used")boolean coupon_used,
			@Param("used_date") LocalDateTime used_date
			);

	@Query("SELECT c FROM UserCoupon c WHERE c.coupon.id = :coupon_id AND c.userId = :userId")
	public Optional<UserCoupon> getUserCouponByCouponAndUser(
			@Param("coupon_id")Long coupon_id,
			@Param("userId")Long userId);
	
	@Query("SELECT c.coupon_used FROM UserCoupon c WHERE c.id = :id")
	public boolean getEnabledById(Long id);
	
	@Query("SELECT c FROM UserCoupon c WHERE c.userId = :userId")
	public  List<UserCoupon> getUserCouponsByUserId(@Param("userId") Long userId);
}
