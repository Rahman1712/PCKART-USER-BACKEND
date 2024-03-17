package com.ar.pckart.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ar.pckart.coupon.model.Coupon;
import com.ar.pckart.coupon.model.UserCoupon;
import com.ar.pckart.coupon.repo.UserCouponRepository;
import com.ar.pckart.user.model.User;

@Service
public class UserCouponService {

	@Autowired private UserCouponRepository userCouponRepo;
	
	public UserCoupon save(UserCoupon userCoupon) {
		return userCouponRepo.save(userCoupon);
	}
	
	public Optional<UserCoupon> findById(Long id){
		return userCouponRepo.findById(id);
	}
	
	public boolean getEnabledById(Long id) {
		return userCouponRepo.getEnabledById(id);
	}
	
	public UserCoupon updateEnabledAndDateById(Long id, LocalDateTime used_date, boolean coupon_used) {
		userCouponRepo.updateCouponUsedAndUsedDateById(id,coupon_used,used_date);
		return findById(id).get();
	}
	
	public Optional<UserCoupon> getUserCouponByCouponAndUser(Coupon coupon, User user) {
		return userCouponRepo.getUserCouponByCouponAndUser(coupon.getId(), user.getId());
	}
	
	public List<UserCoupon> findAllByUser(Long userId){
		return userCouponRepo.getUserCouponsByUserId(userId);
	}

	public UserCoupon checkAndCreateCouponCode(Coupon coupon, Long userId) {
		List<UserCoupon> findAllByUser = findAllByUser(userId);

		for (UserCoupon userCoupon : findAllByUser) {
			if(userCoupon.getCoupon().getId() == coupon.getId()) {
				return userCoupon;
			}
		}
		UserCoupon newUserCoupon = new UserCoupon();
		newUserCoupon.setUserId(userId);
		newUserCoupon.setCoupon(coupon);
		return save(newUserCoupon);
	}
}
