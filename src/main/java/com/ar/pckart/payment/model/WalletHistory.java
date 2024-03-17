package com.ar.pckart.payment.model;

import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.uuid.UuidGenerator;

import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "wallet_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletHistory {
	
	@Id
	@GeneratedValue( strategy = GenerationType.UUID)
	@GenericGenerator(name = "uuid", type = UuidGenerator.class)
	private String id;

    private Double amount;
    
    private Double balance;
    
    private String description;
    
	@Column(name = "transaction_date", 
			nullable = false, updatable = false, insertable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime transactionDate; 

	@Enumerated(EnumType.STRING)
    private WalletTransactionType type;

//	@Enumerated(EnumType.STRING)
//	private PaymentMethod paymentMethod;
	private String razorPaymentId;
	
    @ManyToOne
    @JsonIgnore
    private User user;
}
