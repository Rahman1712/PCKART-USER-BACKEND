package com.ar.pckart.order.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

	private String trackingNo;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;
	private PaymentMethod paymentMethod;
	private Double totalPricePaid;
	private List<TrackStatus> trackStatus;
	
}
