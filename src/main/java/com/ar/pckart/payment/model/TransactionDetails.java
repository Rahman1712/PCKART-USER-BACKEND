package com.ar.pckart.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetails {

   	private String orderId;
	private String currency;
	private Integer amount;
	private String key;
}

/*
  {
    orderId: 'order_MHvzT6digVt8lg', 
    currency: 'INR', 
    amount: 5180000, 
    key: 'rzp_test_K9qFfxNeV2pv2R'
  }
 */