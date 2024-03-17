package com.ar.pckart.user.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ar.pckart.user.model.OtpData;
import com.ar.pckart.user.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface OTPRepository extends JpaRepository<OtpData, Long>{

	public Optional<OtpData> findByUserId(Long id);
	
	public Optional<OtpData> findByUser(User user);

	@Modifying
	@Transactional
    @Query("UPDATE OtpData o SET o.otp = :otp, o.expirationTime = :expirationTime WHERE o.user.id = :userId")
    void updateOtpAndExpirationTimeByUserId(String otp, LocalDateTime expirationTime, Long userId);
	
//	@Query("SELECT o FROM OtpData o WHERE o.user.id = ?1")
//	Optional<OtpData> findByUserId(Long userid);
}
