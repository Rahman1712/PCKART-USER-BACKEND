package com.ar.pckart.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ar.pckart.coupon.model.Coupon;
import com.ar.pckart.coupon.repo.CouponRepository;

@Service
public class CouponService {
	
	@Autowired private CouponRepository couponRepo;
	
	public Coupon save(Coupon coupon) {
		Coupon saved = couponRepo.save(coupon);
		return saved;
	}
	
	public List<Coupon> findAll() {
		return couponRepo.findAll();
	}
	
	public Optional<Coupon> getCouponById(Long id) {
		return couponRepo.findById(id);
	}
	
	public Optional<Coupon> getCouponByCode(String code) {
		return couponRepo.findByCode(code);
	}
	
	public String updateCouponByid(Long id,String code, float discount,LocalDateTime validupto) {
		couponRepo.updateCouponByid(id,code,discount,validupto);
		return "coupon code, validupto , discount updated";
	}
	
	public String updateDiscountAndValidUptoById(Long id, float discount,LocalDateTime validupto) {
		couponRepo.updateDiscountAndValidUpto(id,discount,validupto);
		return "coupon validupto , discount updated";
	}
	
	
	public String updateEnabledById(Long id, boolean enabled) {
		couponRepo.updateEnabledById(id, enabled);
		return "coupon enabled updated";
	}

}
