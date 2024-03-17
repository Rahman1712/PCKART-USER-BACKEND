package com.ar.pckart.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fullname;
	private String houseno;
	private String place;
	private String city;
	private String post;
	private String pincode;
	private String state;
	private String country;
	private String contact;
	private String alternative_contact;
	private boolean selected = false;
//	@ManyToMany(fetch = FetchType.LAZY)
////	@JoinColumn(name = "user_id")
//	@JoinTable(name = "users_address",
//		joinColumns = @JoinColumn(name = "address_id"),
//		inverseJoinColumns = @JoinColumn(name = "user_id")
//			)
//	private Set<User> users = new HashSet<>();
	
//	@JsonIgnore
//	@ManyToMany(mappedBy = "addressList",cascade = CascadeType.ALL)
//	private Set<User> users = new HashSet<>();
}
