package com.ar.pckart.payment.model;

import lombok.*;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.uuid.UuidGenerator;

import com.ar.pckart.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
	
	@Id
	@GeneratedValue( strategy = GenerationType.UUID)
	@GenericGenerator(name = "uuid", type = UuidGenerator.class)
	private String id;

    private Double balance;
    
    @OneToMany(fetch = FetchType.LAZY, 
			targetEntity = WalletHistory.class, 
			cascade = CascadeType.ALL)
	@JoinColumn(name = "wh_fk", referencedColumnName = "id")
    private List<WalletHistory> transactions; 

    @OneToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;
}

