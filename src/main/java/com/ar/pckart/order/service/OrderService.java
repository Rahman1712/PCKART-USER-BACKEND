package com.ar.pckart.order.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.pckart.coupon.model.UserCoupon;
import com.ar.pckart.coupon.service.UserCouponService;
import com.ar.pckart.order.model.Order;
import com.ar.pckart.order.model.OrderDTO;
import com.ar.pckart.order.model.OrderProduct;
import com.ar.pckart.order.model.OrderRequest;
import com.ar.pckart.order.model.OrderStatus;
import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.order.model.PaymentStatus;
import com.ar.pckart.order.model.TrackStatus;
import com.ar.pckart.order.repo.OrderPageRepository;
import com.ar.pckart.order.repo.OrderRepository;
import com.ar.pckart.payment.dto.WalletDto;
import com.ar.pckart.payment.model.Payment;
import com.ar.pckart.payment.model.RazorPayment;
import com.ar.pckart.payment.model.Wallet;
import com.ar.pckart.payment.model.WalletHistory;
import com.ar.pckart.payment.model.WalletTransactionType;
import com.ar.pckart.payment.service.PaymentService;
import com.ar.pckart.payment.service.WalletService;
import com.ar.pckart.product.service.CartService;
import com.ar.pckart.product.service.ProductService;
import com.ar.pckart.user.dto.UserDTO;
import com.ar.pckart.user.service.UserService;

@Service
public class OrderService {

	@Autowired private OrderRepository orderRepo;
	@Autowired private OrderPageRepository orderPageRepo;
	@Autowired private PaymentService paymentService; 
	@Autowired private WalletService walletService; 
	@Autowired private UserCouponService userCouponService;
	@Autowired private CartService cartService;
	@Autowired private ProductService productService;
	@Autowired private UserService userService;
	
	public List<Order> findAll(){
		return orderRepo.findAll();
	}
	
	public List<Order> findAllByUserId(Long userId){
		return orderRepo.findAllByUserId(userId);
	}
	
	public Order findById(String id) {
		return orderRepo.findById(id).get();
	}
	
	public Order saveOrder(Order order) {
		return orderRepo.save(order);
	}
	
	@Transactional
	public Order saveOrderDetails(OrderRequest orderRequest) {
		try {
			Order order = new Order();
			order.setUserId(orderRequest.getUserId());
			order.setOrderAddress(orderRequest.getOrderAddress());
			order.setProducts(orderRequest.getProducts());
			order.setGrandTotalPrice(orderRequest.getGrandTotalPrice());
			order.setShippingCharge(orderRequest.getShippingCharge());
			
			if(orderRequest.getUserCouponId() != 0) {
				UserCoupon userCoupon = userCouponService.updateEnabledAndDateById(orderRequest.getUserCouponId(), LocalDateTime.now(), true);
				order.setUserCoupon(userCoupon);
				order.setCouponDiscount(orderRequest.getCouponDiscount());
			}
			
			order.setTotalPricePaid(orderRequest.getTotalPricePaid());
			
			List<TrackStatus> trackStatusList = new ArrayList<>();
			TrackStatus trackStatusOrdered = TrackStatus.builder()
					.order_status(OrderStatus.ORDERED)
					.description("order placed "+orderRequest.getProducts().size()+" products , using "+orderRequest.getPaymentMethod()+ " payment method.")
					.status_time(LocalDateTime.now())
					.build();
			trackStatusList.add(trackStatusOrdered);
			order.setTrackStatus(trackStatusList);
			
			order.setTrackingNo(generateUniqueTrackingNumber());
			order.setOrderDate(LocalDateTime.now());
			order.setOrderStatus(OrderStatus.ORDERED);
			
			if(orderRequest.getPaymentMethod() == PaymentMethod.ONLINE) {
				RazorPayment paymentSaved = paymentService.saveRazorPayment(orderRequest.getRazorPayment());
				order.setPaymentId(paymentSaved.getRazorpay_payment_id());
			}
			else if(orderRequest.getPaymentMethod() == PaymentMethod.WALLET) {
				WalletDto walletDto = orderRequest.getWalletDto();
				WalletHistory saveHistory = walletService.saveHistory(walletDto, "paid for order with trackno.: "+order.getTrackingNo());
				order.setPaymentId(saveHistory.getId());
			}
			
			if(orderRequest.getPaymentMethod() == PaymentMethod.ONLINE || orderRequest.getPaymentMethod() == PaymentMethod.WALLET) {
				order.setPaymentStatus(PaymentStatus.PAID);
			}else {
				order.setPaymentStatus(PaymentStatus.PENDING);
			}
			order.setPaymentMethod(orderRequest.getPaymentMethod());
	
			Order savedOrder = saveOrder(order);
			
			if(savedOrder.getPaymentMethod() == PaymentMethod.ONLINE || savedOrder.getPaymentMethod() == PaymentMethod.WALLET) {
				Payment payment = Payment.builder()
						.paymentMethod(savedOrder.getPaymentMethod())
						.paymentId(savedOrder.getPaymentId())
						.orderId(savedOrder.getId())
						.build();
				paymentService.savePayment(payment);
			}
			
			String resultString = cartService.deleteCartsByUserIdAndProductId(savedOrder.getUserId(), 
					savedOrder.getProducts()
						.stream() 
						.map(OrderProduct::getProductId)
						.collect(Collectors.toList()));
			System.err.println(resultString);
			
			String resString = productService.updateProductsQuantiy(order.getProducts(), "DELETE");
			System.err.println(resString);
			
			return savedOrder;
		
		} catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error occurred, transaction rolled back.");
	    }
	}
	
	@Transactional
	public Order updateOrderStatusById(String id, OrderStatus orderStatus) {
		orderRepo.updateOrderStatusById(id,orderStatus);
		return findById(id);
	}
	
	public void updatePaymentStatusById(String id, PaymentStatus paymentStatus) {
		orderRepo.updatePaymentStatusById(id,paymentStatus);
	}

	public Optional<Order> findByTrackingNo(String trackingNo) {
		return orderRepo.findByTrackingNo(trackingNo);
	} 
	
	public OrderDTO findOrderByTrackingNo(String trackingNo) {
		Order order = findByTrackingNo(trackingNo).orElse(null);
		
		if(order != null){
			return OrderDTO.builder()
					.trackingNo(order.getTrackingNo())
					.orderDate(order.getOrderDate())
					.orderStatus(order.getOrderStatus())
					.paymentMethod(order.getPaymentMethod())
					.totalPricePaid(order.getTotalPricePaid())
					.trackStatus(order.getTrackStatus())
					.build();
		}else {
			return null;
		}
	}
	
	
    private String generateUniqueTrackingNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomNumber = String.valueOf((int) (Math.random() * 10000));
        return timestamp + "-" + randomNumber;
    }


    public String deleteById(String id) {
    	orderRepo.deleteById(id);
    	return "deleted";
    }

	public List<Order> findAllByLimit(Long limit) {
		if(limit<=10){
			return orderRepo.findTop10ByOrderByOrderDateDesc();
		}else if(limit<=25) {
			return orderRepo.findTop25ByOrderByOrderDateDesc();
		}else if(limit<=50) {
			return orderRepo.findTop50ByOrderByOrderDateDesc();
		}else {
			return orderRepo.findTop100ByOrderByOrderDateDesc();
		}
	}

	@Transactional
	public void updateTrackStatusById(String orderId, TrackStatus track_status) {   // evde anu cancelled manage cheyyunadhu taye returned manage 
		Order order = findById(orderId);
		order.getTrackStatus().add(track_status);
		order.setOrderStatus(track_status.getOrder_status());
		if(track_status.getOrder_status() == OrderStatus.CANCELLED && 
				(order.getPaymentMethod() == PaymentMethod.WALLET || order.getPaymentMethod() == PaymentMethod.ONLINE)) {
			order.setPaymentStatus(PaymentStatus.REFUNDED);
		}
		else if(track_status.getOrder_status() == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
			order.setPaymentStatus(PaymentStatus.PAID);
		}
		else if(track_status.getOrder_status() == OrderStatus.CANCELLED && order.getPaymentStatus() == PaymentStatus.PENDING) {
			order.setPaymentStatus(PaymentStatus.CANCELLED);
		}
		
		orderRepo.save(order);
		
		if(order.getOrderStatus() == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
			Payment payment = Payment.builder()
					.paymentMethod(order.getPaymentMethod())
					.paymentId(order.getPaymentId())
					.orderId(order.getId())
					.build();
			paymentService.savePayment(payment);
		}
		
		if(order.getOrderStatus() == OrderStatus.CANCELLED && 
				(order.getPaymentMethod() == PaymentMethod.ONLINE || order.getPaymentMethod() == PaymentMethod.WALLET)) {
			walletService.creditByUser(userService.getUserById(order.getUserId()), order.getTotalPricePaid(), WalletTransactionType.CREDITED);
		}
		if(order.getOrderStatus() == OrderStatus.CANCELLED) {
			productService.updateProductsQuantiy(order.getProducts(), "ADD");
		}
	}
	
	public Map<String, Object> listAllOrdersWithPagination(int pageNum, int limit, String sortField, String sortDir, String searchKeyword, 
			List<OrderStatus> orderStatusList,List<PaymentStatus> paymentStatusList,List<PaymentMethod> paymentMethodList)
			 {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, limit, sort); // 10 5 20
		
		Page<Order> page = null;
		if(searchKeyword != null && !searchKeyword.trim().equals("")) {
			/*---------- PAGE WITH SEARCH KEYWORD FILTER---------*/
			page = orderPageRepo.getAllOrdersByPaginationWithSearch(
					pageable, orderStatusList, paymentStatusList, paymentMethodList, searchKeyword.trim());
		}else {
			/*---------- PAGE WITHOUT SEARCH---------*/
			page = orderPageRepo.getAllOrdersByPagination(pageable, orderStatusList, paymentStatusList, paymentMethodList);
		}

		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();

		Map<String, Object> map = new HashMap<>();
		map.put("pageNum", pageNum);
		map.put("totalItems", totalItems);
		map.put("totalPages", totalPages);
		map.put("listOrders", page.getContent());
		map.put("sortField", sortField);
		map.put("sortDir", sortDir);
		map.put("limit", limit);
		
		String reverseSortDir =  sortDir.equals("asc") ? "desc" : "asc" ;
		map.put("reverseSortDir", reverseSortDir);
		
		long startCount = (pageNum - 1) * limit + 1;
		map.put("startCount", startCount);
		
		long endCount = (startCount+limit-1) < totalItems ? (startCount+limit-1) : totalItems;
		map.put("endCount", endCount);

		return map;
	}

	@Transactional
	public String returnOrderConfirmed(Order order) {  //evide anu returned manage cheyyunnadhu
		System.err.println(order);
		
		TrackStatus trackStatus = new TrackStatus();
		trackStatus.setOrder_status(OrderStatus.RETURNED);
		trackStatus.setDescription("order return confirmed");
		trackStatus.setStatus_time(LocalDateTime.now());

		order.getTrackStatus().add(trackStatus);
		order.setOrderStatus(OrderStatus.RETURNED);
		order.setPaymentStatus(PaymentStatus.REFUNDED);
		saveOrder(order);
		
		walletService.creditByUser(userService.getUserById(order.getUserId()), order.getTotalPricePaid(), WalletTransactionType.CREDITED);
		
		productService.updateProductsQuantiy(order.getProducts(), "ADD");
		
		return "Order returned Amount Refunded";
	}


	/*============================ COUNT , SUM======================================================*/
	public Long getCountOfOrders() {
		return orderRepo.count();
	}
	
	public Double getTotalSumOfPaidTotalPrice() {
        return orderRepo.findTotalSumOfPaidTotalPrice();
    }
	
	/*============================ RECENT ORDERS BY LIMIT ======================================================*/
	public List<OrderDTO> getTopNumsByOrderByAddedAtDesc(Long limit) {
		List<OrderDTO> orderDtoList = new ArrayList<>();
		List<Order> orderLists = findAllByLimit(limit);
		
		for(Order order: orderLists) {
			var orderDto = OrderDTO.builder()
					.trackingNo(order.getTrackingNo())
					.orderDate(order.getOrderDate())
					.orderStatus(order.getOrderStatus())
					.paymentMethod(order.getPaymentMethod())
					.totalPricePaid(order.getTotalPricePaid())
					.trackStatus(order.getTrackStatus())
					.build();
			
			orderDtoList.add(orderDto);
		}
		
		return orderDtoList;
	}
	
	public List<Order> getOrdersForDay(LocalDateTime date) {
	    LocalDateTime startOfDay = date.with(LocalTime.MIN);
	    LocalDateTime endOfDay = date.with(LocalTime.MAX);
	    return orderRepo.findByOrderDateBetween(startOfDay, endOfDay);
	}
	
	public List<Order> getOrdersForWeek(LocalDateTime date) {
		LocalDateTime startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
		LocalDateTime endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
				.with(LocalTime.MAX);
		return orderRepo.findByOrderDateBetween(startOfWeek, endOfWeek);
	}
	
	public List<Order> getOrdersForMonth(LocalDateTime date) {
		LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
		LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
		return orderRepo.findByOrderDateBetween(startOfMonth, endOfMonth);
	}
	public List<Order> getOrdersForYear(LocalDateTime date) {
        LocalDateTime startOfYear = date.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
        LocalDateTime endOfYear = date.with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
        return orderRepo.findByOrderDateBetween(startOfYear, endOfYear);
    }
	
	//=====GET ORDERS BY DATE========================
	public List<Object[]> getDayOrderTotals(){
		return orderRepo.getDayOrderTotals();
	}
	public List<Object[]> getWeekOrderTotals(){
		return orderRepo.getWeekOrderTotals();
	}
	public List<Object[]> getMonthOrderTotals(){
		return orderRepo.getMonthOrderTotals();
	}
	public List<Object[]> getYearOrderTotals(){
		return orderRepo.getYearOrderTotals();
	}
	
	public List<Object[]> getMostSellProducts(){
		return orderRepo.getMostSellProducts();
	}
	
	public List<Object[]> getMostOrderProductsQuantity(Long limit){
		return orderRepo.getMostOrderProductsQuantity(limit);
	}
	
	public List<Object[]> getOrderStatusCounts(){
		return orderRepo.getOrderStatusCounts();
	}
//	===================
	public List<Object[]> getDayOrderAllDetails(String duration){
		switch (duration) {
		case "DAILY": {
			return orderRepo.getDayOrderAllDetails();
		}
		case "MONTHLY": {
			return orderRepo.getMonthOrderAllDetails();
		}
		case "YEARLY": {
			return orderRepo.getYearOrderAllDetails();
		}
		default:
			return orderRepo.getDayOrderAllDetails();
		}	
	}
	
	public Map<String, Object> getDayOrderAllDetailsPagination(int pageNum, int limit, String sortField, String sortDir, String duration){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, limit, sort); // 10 5 20
		
		Page<Object[]> page = null;
		switch (duration) {
			case "DAILY": {
				page = orderPageRepo.getDayOrderAllDetails(pageable);
				break;
			}
			case "MONTHLY": {
				page = orderPageRepo.getMonthOrderAllDetails(pageable);
				break;
			}
			case "YEARLY": {
				page = orderPageRepo.getYearOrderAllDetails(pageable);
				break;
			}
			default:
				page = orderPageRepo.getDayOrderAllDetails(pageable);
				break;
		}
		
		
		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();

		Map<String, Object> map = new HashMap<>();
		map.put("pageNum", pageNum);
		map.put("totalItems", totalItems);
		map.put("totalPages", totalPages);
		map.put("listOrders", page.getContent());
		map.put("sortField", sortField);
		map.put("sortDir", sortDir);
		map.put("limit", limit);
		
		String reverseSortDir =  sortDir.equals("asc") ? "desc" : "asc" ;
		map.put("reverseSortDir", reverseSortDir);
		
		long startCount = (pageNum - 1) * limit + 1;
		map.put("startCount", startCount);
		
		long endCount = (startCount+limit-1) < totalItems ? (startCount+limit-1) : totalItems;
		map.put("endCount", endCount);

		return map;
	}


}
