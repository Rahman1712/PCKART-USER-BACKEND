package com.ar.pckart.payment.dto;

import lombok.*;
import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.user.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WalletDto {
	
    private Double amount;
    private WalletTransactionType walletTransactionType;
    private User user;
}
