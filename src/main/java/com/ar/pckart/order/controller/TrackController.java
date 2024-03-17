package com.ar.pckart.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.order.model.OrderDTO;
import com.ar.pckart.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TrackController {

	private final OrderService orderService;
	
	@GetMapping("/trackstatus/{trackingNo}")
	public ResponseEntity<OrderDTO> getTrackDetail(
			@PathVariable("trackingNo") String trackingNo){
		return ResponseEntity.ok(orderService.findOrderByTrackingNo(trackingNo));
	}
}
