package com.ar.pckart.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.payment.service.WalletService;
import com.ar.pckart.user.model.User;
import com.ar.pckart.user.service.UserService;

@RestController
@RequestMapping("/pckart/api/v1/user-to-admin/payment")
public class AdminPaymentController {

    @Autowired private WalletService walletService;
    @Autowired private UserService userService;
	
	@PutMapping("/credit/byUserId/{userId}")
    public ResponseEntity<String> creditByUserId(
    		@PathVariable("userId") Long userId,
    		@RequestParam("amount") Double amount,
    		@RequestParam("walletTransactionType") WalletTransactionType walletTransactionType
    		){
        if (amount < 1){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid amount");
        }
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok().body(walletService.creditByUser(userById, amount, null));
    }
}
