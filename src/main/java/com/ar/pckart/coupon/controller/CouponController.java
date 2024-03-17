package com.ar.pckart.coupon.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.coupon.model.Coupon;
import com.ar.pckart.coupon.model.UserCoupon;
import com.ar.pckart.coupon.service.CouponService;
import com.ar.pckart.coupon.service.UserCouponService;

@RestController
@RequestMapping("/pckart/api/v1/coupon")
public class CouponController {
	
	@Autowired
	private CouponService couponService;
	@Autowired
	private UserCouponService userCouponService;
	
	@PostMapping("/check_and_create/coupon/{code}")
	public ResponseEntity<?> checkAndCreateByCode(@PathVariable("code")String code,
			@RequestParam("userId")Long userId 
			) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", code);
		
		Optional<Coupon> couponByCode = couponService.getCouponByCode(code);
		if(couponByCode.isPresent()) {
			UserCoupon userCoupon = userCouponService.checkAndCreateCouponCode(couponByCode.get(), userId);
			boolean valid = userCoupon.getCoupon().getValidupto().isAfter(LocalDateTime.now());
			System.err.println(valid);
			result.put("valid", valid);
			result.put("user_coupon_id", userCoupon.getId());
			result.put("is_used", userCoupon.isCoupon_used());
			result.put("coupon", userCoupon.getCoupon());
			result.put("message", valid? "valid coupon":"coupon expired");
		}else {
			result.put("valid", false);
			result.put("message", "not a valid coupon");
		}
		
		return ResponseEntity.ok(result);
	}
}
