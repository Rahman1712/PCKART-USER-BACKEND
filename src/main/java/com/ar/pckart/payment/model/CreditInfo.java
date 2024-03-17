package com.ar.pckart.payment.model;

import com.ar.pckart.payment.dto.WalletDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditInfo {

	private WalletDto walletDto;
	private RazorPayment razorPayment;
}
