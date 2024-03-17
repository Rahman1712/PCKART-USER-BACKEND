package com.ar.pckart.payment.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.payment.dto.WalletDto;
import com.ar.pckart.payment.model.Payment;
import com.ar.pckart.payment.model.RazorPayment;
import com.ar.pckart.payment.model.Wallet;
import com.ar.pckart.payment.model.WalletHistory;
import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.payment.repo.WalletHistoryRepository;
import com.ar.pckart.payment.repo.WalletRepository;
import com.ar.pckart.user.model.User;

@Service
public class WalletService {

	@Autowired
	private WalletRepository walletRepository;
	@Autowired
	private WalletHistoryRepository walletHistoryRepository;
	@Autowired
	private PaymentService paymentService;

	public Wallet getById(String id) {
		return walletRepository.findById(id).orElse(null);
	}

	public Wallet findByUser(User user) {
		return walletRepository.findByUser(user);
	}
	
	public Double findBalanceByUser(User user) {
		return walletRepository.findBalanceByUser(user);
	}

	public Wallet save(Wallet wallet) {
		return walletRepository.save(wallet);
	}
	
	public WalletHistory saveHistory(WalletDto walletDto, String description) {
		Double balance =  0.0 ;
		if(walletDto.getWalletTransactionType() == WalletTransactionType.CREDITED) balance = findBalanceByUser(walletDto.getUser()) + walletDto.getAmount();
		else if(walletDto.getWalletTransactionType() == WalletTransactionType.DEBITED)  balance = findBalanceByUser(walletDto.getUser()) - walletDto.getAmount();
		
		WalletHistory walletHistory = WalletHistory.builder()
				.amount(walletDto.getAmount())
				.balance(balance)
				.description(description)
				.type(walletDto.getWalletTransactionType())
				.user(walletDto.getUser())
				.build();
		
		Wallet wallet = findByUser(walletDto.getUser());
		wallet.setBalance(wallet.getBalance() - walletDto.getAmount());
		walletRepository.save(wallet);
		
		return walletHistoryRepository.save(walletHistory);
	}
	 
	@Transactional
	public String credit(User user, Double amount, RazorPayment razorPayment) {
		RazorPayment paymentSaved = paymentService.saveRazorPayment(razorPayment);
		
		Wallet wallet = findByUser(user);
		Double newBalance = wallet.getBalance() + amount;
		wallet.setBalance(newBalance);
		wallet.getTransactions().add(transfer(user, WalletTransactionType.CREDITED, 
				amount, newBalance, "amount added to wallet", paymentSaved.getRazorpay_payment_id()));
		Wallet savedWallet = walletRepository.save(wallet);
		
    	
		Payment payment = Payment.builder()
				.paymentMethod(PaymentMethod.WALLET)
				.paymentId(savedWallet.getId())
				.build();
		paymentService.savePayment(payment); 
		
		return "Credit saved";
	} 
	
	public String creditByUser(User user, Double amount, WalletTransactionType walletTransactionType) {
		
		Wallet wallet = findByUser(user);
		Double newBalance = wallet.getBalance() + amount;
		wallet.setBalance(newBalance);
		wallet.getTransactions().add(transfer(user, WalletTransactionType.CREDITED, 
				amount, newBalance, "amount refunded to wallet", null));
		Wallet savedWallet = walletRepository.save(wallet);
		
    	
		Payment payment = Payment.builder()
				.paymentMethod(PaymentMethod.WALLET)
				.paymentId(savedWallet.getId())
				.build();
		paymentService.savePayment(payment); 
		
		return "Credit : Amount Refunded";
	}
	
	public String debit(User user, Double amount) {
		Wallet wallet = findByUser(user);
		Double newBalance = wallet.getBalance() - amount;
		wallet.setBalance(newBalance);
		wallet.getTransactions().add(transfer(user, WalletTransactionType.DEBITED, amount, newBalance, "amount debited from wallet", null));
		Wallet savedWallet = walletRepository.save(wallet);
		
		Payment payment = Payment.builder()
				.paymentMethod(PaymentMethod.WALLET)
				.paymentId(savedWallet.getId())
				.build();
		paymentService.savePayment(payment);
		
		return "Debit saved";
	}


	public WalletHistory recordTransaction(WalletHistory walletHistory) {
		return walletHistoryRepository.save(walletHistory);
	}

	public List<WalletHistory> findAllByUser(User user){
		return walletHistoryRepository.findAllByUser(user);
	}


	public Page<WalletHistory> findAllByUser(User user, Pageable pageable){
		return walletHistoryRepository.findAllByUser(user, pageable);
	}

	private WalletHistory transfer(
			User user, WalletTransactionType walletTransactionType, 
			Double amount, Double balance, 
			String description, String paymentId) {
		
		return  WalletHistory.builder()
				.amount(amount)
				.balance(balance)
				.description(description)
				.type(walletTransactionType)
				.transactionDate(LocalDateTime.now())
				.razorPaymentId(paymentId)
				.user(user)
				.build();
	}
	
	/*============================ PAGE ======================================================*/
	public Map<String, Object> walletHistoryListWithPagination(int pageNum, int limit, String sortField, String sortDir, User user)
			throws IOException {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, limit, sort); // 10 5 20
		
		Page<WalletHistory> page = findAllByUser(user, pageable);

		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();

		Map<String, Object> map = new HashMap<>();
		map.put("pageNum", pageNum);
		map.put("totalItems", totalItems);
		map.put("totalPages", totalPages);
		map.put("walletHistoryList", page.getContent());
		map.put("sortField", sortField);
		map.put("sortDir", sortDir);
		map.put("limit", limit);
		
		String reverseSortDir =  sortDir.equals("asc") ? "desc" : "asc" ;
		map.put("reverseSortDir", reverseSortDir);
		
		long startCount = (pageNum - 1) * limit + 1;
		map.put("startCount", startCount);
		
		long endCount = (startCount+limit-1) < totalItems ? (startCount+limit-1) : totalItems;
		map.put("endCount", endCount);

		return map;
	}

}
