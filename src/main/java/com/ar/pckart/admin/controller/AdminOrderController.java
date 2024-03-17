package com.ar.pckart.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.order.model.Order;
import com.ar.pckart.order.model.OrderStatus;
import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.order.model.PaymentStatus;
import com.ar.pckart.order.model.TrackStatus;
import com.ar.pckart.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pckart/api/v1/user-to-admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

	private final OrderService orderService;
	
	@GetMapping("/get/allOrders")
	public ResponseEntity<List<Order>> getAllOrders(){
		return ResponseEntity.ok(orderService.findAll());
	}
	
	@GetMapping("/get/orders/bylimit/{limit}")
	public ResponseEntity<List<Order>> getAllOrdersByLimit(
			@PathVariable("limit") Long limit
			){
		System.err.println("DDDDDDDDDDDDDDDD");
		List<Order> findAllByLimit = orderService.findAllByLimit(limit);
		System.err.println(findAllByLimit);
		return ResponseEntity.ok(findAllByLimit);
	}
	
	@GetMapping("/get/order/byid/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable("id")String id){
		return ResponseEntity.ok(orderService.findById(id));
	}
	
	@GetMapping("/get/order/byTrackingNo/{trackingNo}")
	public ResponseEntity<Order> getOrderByTrackingNo(@PathVariable("trackingNo")String trackingNo){
		return ResponseEntity.ok(orderService.findByTrackingNo(trackingNo).get());
	}
	
	@GetMapping("/get/orders/byUserId/{userId}")
	public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable("userId")Long userId){
		return ResponseEntity.ok(orderService.findAllByUserId(userId));
	}
	
	@PutMapping("/update/payment_status/byid/{orderId}")
	public ResponseEntity<String> updatePaymentStatusById(@PathVariable("orderId")String orderId,
			@RequestParam("payment_status") String payment_status){
		orderService.updatePaymentStatusById(orderId, PaymentStatus.valueOf(payment_status));
		return ResponseEntity.ok("PAYMENT STATUS UPDATED");
	}
	
	@PutMapping("/update/order_status/byid/{orderId}")
	public ResponseEntity<String> updateOrderStatusById(@PathVariable("orderId")String orderId,
			@RequestParam("order_status") String order_status){
		orderService.updateOrderStatusById(orderId, OrderStatus.valueOf(order_status));
		return ResponseEntity.ok("ORDER STATUS UPDATED");
	}
	
	@PutMapping("/update/track_status/byid/{orderId}")
	public ResponseEntity<String> updateTrackStatusById(@PathVariable("orderId")String orderId,
			@RequestBody TrackStatus track_status){
		orderService.updateTrackStatusById(orderId, track_status);
		return ResponseEntity.ok("TRACK STATUS UPDATED");
	}
	
	@GetMapping("/get/orders-page/{pageNum}")
	public Map<String, Object> listAllOrdersWithPagination(
			@PathVariable("pageNum") int pageNum , 
			@RequestParam("limit") int limit,
			@RequestParam("sortField") String sortField , 
			@RequestParam("sortDir") String sortDir ,
			@RequestParam("searchKeyword") String searchKeyword,
			@RequestParam("orderStatusList") List<OrderStatus> orderStatusList,
			@RequestParam("paymentStatusList") List<PaymentStatus> paymentStatusList,
			@RequestParam("paymentMethodList") List<PaymentMethod> paymentMethodList
			) throws IOException {

		Map<String, Object> map = orderService.listAllOrdersWithPagination(
				pageNum, limit, sortField, sortDir, searchKeyword, 
				orderStatusList, paymentStatusList, paymentMethodList);
		System.err.println(map);
		return map;
	}
	
	@PutMapping("/update/return-order")
	public ResponseEntity<String> returnOrderConfirmed(@RequestBody Order order) {
		return ResponseEntity.ok(orderService.returnOrderConfirmed(order));
	}
	
}
