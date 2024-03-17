package com.ar.pckart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishResponse {

	private Long wishId;
	private Long userId;
	private Long productId;
}
