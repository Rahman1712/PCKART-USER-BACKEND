package com.ar.pckart.user.service;

import org.springframework.stereotype.Service;

import com.ar.pckart.user.model.OtpData;
import com.ar.pckart.user.model.User;
import com.ar.pckart.user.repo.OTPRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private final OTPRepository otpRepository;

    public String generateOTP(User user){
        // Generate a random OTP
        String otp = generateRandomOTP(OTP_LENGTH);

        // Calculate the expiration time
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        // Store the OTP in the database
        OtpData otpData = OtpData.builder()
        		.otp(otp)
        		.expirationTime(expirationTime)
        		.user(user)
        		.build();
        
        Optional<OtpData> findByUser = otpRepository.findByUser(user);
        if(findByUser.isPresent()) {
        	otpRepository.updateOtpAndExpirationTimeByUserId(otp, expirationTime, user.getId());
        }else{
        	otpRepository.save(otpData);
        }

        return otp;
    }

    public boolean verifyOTP(User user, String enteredOTP) {  	
        OtpData otpData = otpRepository.findByUserId(user.getId()).get();
        if (otpData == null) {
        	throw new UserLoginException("otp is not valid");
            //return false; // OTP data not found
        }

        // Check if the OTP is expired
        if (otpData.getExpirationTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpData); // Remove expired OTP data
            throw new UserLoginException("otp has expired, not valid");
            //return false; // OTP expired
        }

        boolean isOTPVerified = otpData.getOtp().equals(enteredOTP);

        if (isOTPVerified) {
            otpRepository.delete(otpData); // Remove verified OTP data
        }

        return isOTPVerified;
    }

    private String generateRandomOTP(int length) {
        // Generate a random OTP of the specified length
        Random random = new Random();
        StringBuilder otpBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            otpBuilder.append(digit);
        }

        return otpBuilder.toString();
    }
    
    public Optional<OtpData> getOtpDataByUserId(Long userId){
    	return otpRepository.findByUserId(userId);
    }
    
    public void deleteOtpData(OtpData otpData) {
    	otpRepository.delete(otpData);
    }
}
