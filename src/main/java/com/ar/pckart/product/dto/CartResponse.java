package com.ar.pckart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

	private Long cartId;
	private Long userId;
	private Long productId;
	private int quantity;
}
