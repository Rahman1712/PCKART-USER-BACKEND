package com.ar.pckart.admin.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.coupon.model.Coupon;
import com.ar.pckart.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pckart/api/v1/user-to-admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {

	private final CouponService couponService;

	@PostMapping("/save")
	public ResponseEntity<Coupon> saveCoupon(@RequestBody Coupon coupon){
		return ResponseEntity.ok(couponService.save(coupon));
	}
	
	@GetMapping("/get/coupon/all")
	public ResponseEntity<List<Coupon>> getAllCoupons(){
		return ResponseEntity.ok(couponService.findAll());
	}
	
	@GetMapping("/get/coupon/byid/{id}")
	public ResponseEntity<Coupon> getCouponByid(@PathVariable("id") Long id){
		Coupon coupon = couponService.getCouponById(id).get();
		return ResponseEntity.ok(coupon);
	}
	
	@PutMapping("/update/coupondata/byid/{couponId}")
	public ResponseEntity<String> updateCouponDataById(
			@PathVariable("couponId")Long couponId,
			@RequestParam("code")String code,
			@RequestParam("discount")float discount,
			@RequestParam("validupto")LocalDateTime validupto){
		return ResponseEntity.ok(couponService.updateCouponByid(couponId, code, discount, validupto));
	}
	
	@PutMapping("/update/enabled/byid/{couponId}")
	public ResponseEntity<String> updateEnabledById(@PathVariable("couponId")Long couponId,
			@RequestParam("enabled") boolean enabled){
		return ResponseEntity.ok(couponService.updateEnabledById(couponId, enabled));
	}
	
	@PutMapping("/update/validupto_discount/byid/{couponId}")
	public ResponseEntity<String> updateValiduptoDiscount(@PathVariable("couponId")Long couponId,
			@RequestParam("discount")float discount,@RequestParam("validupto")LocalDateTime validupto){
		return ResponseEntity.ok(couponService.updateDiscountAndValidUptoById(couponId, discount,validupto));
	}
}
