package com.ar.pckart.order.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ar.pckart.order.model.Order;
import com.ar.pckart.order.model.OrderRequest;
import com.ar.pckart.order.model.OrderStatus;
import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.order.model.TrackStatus;
import com.ar.pckart.order.service.OrderService;
import com.ar.pckart.payment.model.TransactionDetails;
import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.payment.service.PaymentService;
import com.ar.pckart.payment.service.WalletService;
import com.ar.pckart.product.service.ProductService;
import com.ar.pckart.user.model.User;
import com.ar.pckart.user.service.UserService;

@RestController
@RequestMapping("/pckart/api/v1/order")
public class OrderController {
	
	@Autowired OrderService orderService;
	@Autowired PaymentService paymentService;
	@Autowired WalletService walletService;
	@Autowired UserService userService;
	@Autowired ProductService productService;

	@GetMapping("/createTransaction/{amount}")
	public TransactionDetails createTransaction(@PathVariable(name = "amount") Double amount) {
		return paymentService.createTransaction(amount);
	}
	
	@PostMapping("/saveOrder")
	public ResponseEntity<Order> saveOrder(@RequestBody OrderRequest orderRequest) {
		System.err.println(orderRequest);
		Order savedOrder = orderService.saveOrderDetails(orderRequest);
		return ResponseEntity.ok(savedOrder);
	}
	
	@GetMapping("/get/order/byTrackingNo/{trackingNo}")
	public ResponseEntity<?> getOrderByTrackingNo(@PathVariable("trackingNo")String trackingNo){
		Optional<Order> order = orderService.findByTrackingNo(trackingNo);
		if(!order.isPresent()) {
			return ResponseEntity.badRequest().body("Not a valid tracking number");

		}
		return ResponseEntity.ok(order.get());
	}
	
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {
		return orderService.deleteById(id);
	}
	
	@GetMapping("/get/orders/byUserId/{userId}")
	public ResponseEntity<List<Order>> getOrdersByUserID(
			@PathVariable("userId")Long userId){
		return ResponseEntity.ok(orderService.findAllByUserId(userId));
	}
	
	@GetMapping("/get/order/byOrderId/{orderId}")
	public ResponseEntity<Order> getOrderByOrderID(
			@PathVariable("orderId")String orderId){
		Order orderFound = orderService.findById(orderId);
		System.err.println(orderFound);
		return ResponseEntity.ok(orderFound);
	}
	
	@PutMapping("/update/order_status/byid/{orderId}")
	public ResponseEntity<String> updateOrderStatusById(@PathVariable("orderId")String orderId,
			@RequestParam("order_status") OrderStatus order_status){
		Order order = orderService.updateOrderStatusById(orderId, order_status);
		//create cancel track status
		if(order_status == OrderStatus.CANCELLED) {
			TrackStatus trackStatus = new TrackStatus();
			trackStatus.setOrder_status(order_status);
			trackStatus.setDescription("order cancelled");
			trackStatus.setStatus_time(LocalDateTime.now());
			orderService.updateTrackStatusById(orderId, trackStatus);	
		}
		else if(order_status == OrderStatus.RETURN_REQUESTED) {
			TrackStatus trackStatus = new TrackStatus();
			trackStatus.setOrder_status(order_status);
			trackStatus.setDescription("requested for returning order");
			trackStatus.setStatus_time(LocalDateTime.now());
			orderService.updateTrackStatusById(orderId, trackStatus);
		}
		
		if(order_status == OrderStatus.CANCELLED && order.getPaymentMethod() == PaymentMethod.WALLET) {
			User user = userService.getUserById(order.getUserId());
			walletService.creditByUser(user, order.getTotalPricePaid(), WalletTransactionType.CREDITED);
		}

		return ResponseEntity.ok("ORDER STATUS UPDATED");
	}
	
	
}

//@Autowired OrderPaymentService orderPaymentService;

//@GetMapping("/createTransaction/{amount}")
//public TransactionDetails createTransaction(
//		@PathVariable(name = "amount") Double amount,
//		HttpServletRequest request,
//		HttpServletResponse response
//		) throws IOException {
//	
//	return orderPaymentService.createTransaction(amount,request,response);
//}
