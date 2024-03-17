package com.ar.pckart.payment.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ar.pckart.payment.model.Payment;
import com.ar.pckart.payment.model.RazorPayment;
import com.ar.pckart.payment.model.TransactionDetails;
import com.ar.pckart.payment.repo.PaymentRepository;
import com.ar.pckart.payment.repo.RazorPaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentService {
	
	@Value("${razorpay.api.key}")
	private String KEY;
	@Value("${razorpay.api.key_secret}")
	private String KEY_SECRET;
	@Value("${razorpay.api.currency}")
	private String CURRENCY;
	
	@Autowired private PaymentRepository paymentRepo; 
	@Autowired private RazorPaymentRepository razorPaymentRepository;

	//100dolar -> ennadhu 100cents , ie eatavum cheriya vilayanu kanakkaakuka paise 
	public TransactionDetails createTransaction(Double amount) {
		try {
			RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("amount", (amount * 100));
			jsonObject.put("currency", CURRENCY);
			
			
			Order order = razorpayClient.orders.create(jsonObject);
			
			System.err.println(order);
			
			return prepareTransactionDetails(order);
		} catch (RazorpayException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private TransactionDetails prepareTransactionDetails(Order order) {
		String orderId = order.get("id");
		String currency = order.get("currency");
		Integer amount = order.get("amount");
		
		TransactionDetails transactionDetails = new TransactionDetails(
				orderId, currency, amount, KEY);
		return transactionDetails;
	}

	public RazorPayment saveRazorPayment(RazorPayment razorPayment) {
		return razorPaymentRepository.save(razorPayment);
	}
	
	public Payment savePayment(Payment payment) {
		return paymentRepo.save(payment);
	}
}

//private static final String KEY = "rzp_test_K9qFfxNeV2pv2R";
//private static final String KEY_SECRET = "5OJiiEeTHlvsvDuC8v5N9pDX";
//private static final String CURRENCY = "INR";



//{"amount":120000,"amount_paid":0,"notes":[],"created_at":1689779318,"amount_due":120000,"currency":"INR","receipt":null,"id":"order_MFhhj2TWBFFkEa","entity":"order","offer_id":null,"status":"created","attempts":0}

//amount
//currency
//key
//secret key