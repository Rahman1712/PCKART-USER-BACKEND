package com.ar.pckart.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_products")
public class OrderProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "product_id" , nullable = false)
	private Long productId;
	
	@Column(name = "product_name" , nullable = false)
	private String productName;
	
	@Column(name = "product_price" , nullable = false)
	private float productPrice;
	
	@Column(name = "product_quantity" , nullable = false)
	private int productQuantity;
	
	@Column(name = "product_brand")
	private String brand;
	@Column(name = "product_category")
	private String category;
	@Column(name = "product_color")
	private String color;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "order_id")
//	private Order order;
}
