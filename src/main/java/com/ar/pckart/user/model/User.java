package com.ar.pckart.user.model;

import java.util.List;
import java.util.Set;

import com.ar.pckart.payment.model.Wallet;
import com.ar.pckart.payment.model.WalletHistory;
import com.ar.pckart.product.model.Cart;
import com.ar.pckart.product.model.Wish;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fullname;
	@Column(unique = true)
	private String email;
	@Column(unique = true)
	private String mobile;
	@Column(unique = true, nullable = false)
	private String username;
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<Token> tokens;
	
	@Lob 
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "admin_image",length=100000)
	private byte[] image;
	
	@Column(name = "admin_image_name")
	private String imageName;
	
	@Column(name = "admin_image_type")
	private String imageType;
	
	private boolean enabled;
	
	private boolean nonLocked;
	
	@OneToMany(fetch = FetchType.LAZY, 
			targetEntity = Address.class, 
			cascade = CascadeType.ALL)
	@JoinColumn(name = "cp_fk", referencedColumnName = "id")
	private List<Address> addresses; 
	
	@JsonIgnore
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	private Set<Cart> cart;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
	private Set<Wish> wish;
	
	@JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

}


//private List<WalletHistory> transactions = new ArrayList<>();
//private Set<Cart> cart = new HashSet<>();
//private Set<Wish> wish = new HashSet<>();

/*

	@JsonIgnore
	@ManyToMany(mappedBy = "user",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(name = "address_list",
	joinColumns = {
			@JoinColumn(name = "user_id")
	},
		inverseJoinColumns = {
				@JoinColumn(name = "address_id")
		}
	)
	private Set<Address> addresses = new HashSet<>();
*/