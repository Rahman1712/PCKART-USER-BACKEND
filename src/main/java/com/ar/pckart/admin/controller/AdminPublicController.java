package com.ar.pckart.admin.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.order.service.OrderService;
import com.ar.pckart.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminPublicController {

	private final UserService userService;
	private final OrderService orderService;
	
	@GetMapping("/get/user-count")
	public ResponseEntity<?> countOfProducts(){
		return ResponseEntity.ok(userService.getCountOfUsers());
	}
	
	@GetMapping("/get/order-count")
	public ResponseEntity<?> countOfOrders(){
		return ResponseEntity.ok(orderService.getCountOfOrders());
	}
	
	@GetMapping("/get/order-revenue")
	public ResponseEntity<?> getTotalSumOfPaidTotalPrice(){
		return ResponseEntity.ok(orderService.getTotalSumOfPaidTotalPrice());
	}
	
	@GetMapping("/get/recent-orders/{limit}")
	public ResponseEntity<?> recentOrders(@PathVariable("limit")Long limit){
		var ordersDtoList = orderService.getTopNumsByOrderByAddedAtDesc(limit);
		return ResponseEntity.ok(ordersDtoList);
//		return ResponseEntity.status(HttpStatus.OK).body(ordersDtoList);
	}

	@GetMapping("/get/orders/byday")
	public ResponseEntity<?> getDayOrderTotals(){
		return ResponseEntity.ok(orderService.getDayOrderTotals());
	}
	@GetMapping("/get/orders/byweek")
	public ResponseEntity<?> getWeekOrderTotals(){
		return ResponseEntity.ok(orderService.getWeekOrderTotals());
	}
	@GetMapping("/get/orders/bymonth")
	public ResponseEntity<?> getMonthOrderTotals(){
		return ResponseEntity.ok(orderService.getMonthOrderTotals());
	}
	@GetMapping("/get/orders/byyear")
	public ResponseEntity<?> getYearOrderTotals(){
		return ResponseEntity.ok(orderService.getYearOrderTotals());
	}
	
	@GetMapping("/get/orders-most")
	public ResponseEntity<?> getMostSellProducts(){
		return ResponseEntity.ok(orderService.getMostSellProducts());
	}
	
	@GetMapping("/get/orders-more-qty/{limit}")
	public ResponseEntity<?> getMostOrderProductsQuantity(@PathVariable("limit")Long limit){
		return ResponseEntity.ok(orderService.getMostOrderProductsQuantity(limit));
	}
	
	@GetMapping("/get/order-status-count")
	public ResponseEntity<?> getOrderStatusCounts(){
		return ResponseEntity.ok(orderService.getOrderStatusCounts());
	}
	
//	===============
	@GetMapping("/get/orders-all")
	public ResponseEntity<?> getDayOrderAllDetails(@RequestParam("duration") String duration){
		return ResponseEntity.ok(orderService.getDayOrderAllDetails(duration));
	}
	
	@GetMapping("/get/orders-all-page/{pageNum}")
	public Map<String, Object> getDayOrderAllDetailsPagination(
			@PathVariable("pageNum") int pageNum , 
			@RequestParam("limit") int limit,
			@RequestParam("sortField") String sortField , 
			@RequestParam("sortDir") String sortDir, 
			@RequestParam("duration") String duration 
			) throws IOException {

		Map<String, Object> map = orderService.getDayOrderAllDetailsPagination(pageNum, limit, sortField, sortDir, duration);
		return map;
	}
}
