package com.ar.pckart.payment.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ar.pckart.payment.dto.WalletDto;
import com.ar.pckart.payment.model.CreditInfo;
import com.ar.pckart.payment.model.RazorPayment;
import com.ar.pckart.payment.model.TransactionDetails;
import com.ar.pckart.payment.model.Wallet;
import com.ar.pckart.payment.model.WalletHistory;
import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.payment.service.PaymentService;
import com.ar.pckart.payment.service.WalletService;
import com.ar.pckart.user.model.User;
import com.ar.pckart.user.service.UserService;

@RestController
@RequestMapping("/pckart/api/v1/wallet")
public class WalletController {

    @Autowired private WalletService walletService;
    @Autowired private UserService userService;
    @Autowired private PaymentService paymentService;

    @PostMapping("/create/byuserId/{userId}")
    public ResponseEntity<?> create(@PathVariable("userId") Long userId){
    	User userById = userService.getUserById(userId);
    	Wallet walletNew = Wallet.builder()
    	.balance(0.0)
    	.user(userById)
    	.build();
        return ResponseEntity.ok().body(walletService.save(walletNew));
    }
    
	@GetMapping("/createTransaction/{amount}")
	public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
		return paymentService.createTransaction(amount);
	}
    
    @PostMapping("/credit")
    public ResponseEntity<String> credit(
    		@RequestBody CreditInfo creditInfo
    		){
    	System.err.println(creditInfo);
    	WalletDto walletDto = creditInfo.getWalletDto();
    	RazorPayment razorPayment = creditInfo.getRazorPayment();
    	
    	if (walletDto.getAmount() < 1){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid amount");
        }
   
        return ResponseEntity.ok().body(walletService.credit(walletDto.getUser(), walletDto.getAmount(), razorPayment));
    }

    @PutMapping("/debit")
    public ResponseEntity<String> debit(@RequestBody WalletDto walletDto){
        if (walletDto.getAmount() < 1){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid amount");
        }
        return ResponseEntity.ok().body(walletService.debit(walletDto.getUser(), walletDto.getAmount()));
    }
    
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

    @GetMapping("/balance/byuserId/{userId}")
    public ResponseEntity<Double> getBalanceByUserId(@PathVariable("userId") Long userId){
    	User userById = userService.getUserById(userId);
        return ResponseEntity.ok().body(walletService.findBalanceByUser(userById));
    }
    
    @GetMapping("/get/byuserId/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable("userId") Long userId){
    	User userById = userService.getUserById(userId);
        return ResponseEntity.ok().body(walletService.findByUser(userById));
    }
    
    @GetMapping("/get/history/byuserId/{userId}")
    public ResponseEntity<List<WalletHistory>> getWalletHistoryByUserId(@PathVariable("userId") Long userId){
    	User userById = userService.getUserById(userId);
        return ResponseEntity.ok().body(walletService.findAllByUser(userById));
    }
    
	@GetMapping("/get/wallet-history/{pageNum}" )
	public Map<String, Object> walletHistoryListWithPagination(
			@PathVariable("pageNum") int pageNum , 
			@Param("limit") int limit,
			@Param("sortField") String sortField , 
			@Param("sortDir") String sortDir ,
			@Param("userId") Long userId) throws IOException {
		
		User userById = userService.getUserById(userId);
		Map<String, Object> map = walletService.walletHistoryListWithPagination(pageNum,limit, sortField,sortDir,userById );
		
		
		return map;
	}

}
