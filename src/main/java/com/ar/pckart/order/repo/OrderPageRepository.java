package com.ar.pckart.order.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.order.model.Order;
import com.ar.pckart.order.model.OrderStatus;
import com.ar.pckart.order.model.PaymentMethod;
import com.ar.pckart.order.model.PaymentStatus;

@Repository
public interface OrderPageRepository extends PagingAndSortingRepository<Order, String>{
	
	@Query("SELECT o FROM Order o "
			+ "WHERE "
	        + "o.orderStatus IN :orderStatusList "
	        + "AND o.paymentStatus IN :paymentStatusList "
	        + "AND o.paymentMethod IN :paymentMethodList "
			+ "AND CONCAT(o.id, ' ',o.trackingNo,' ',o.orderAddress.fullname) "
			+ "LIKE %:searchKeyword%") 
	public Page<Order> getAllOrdersByPaginationWithSearch(
			Pageable pageable,
			@Param("orderStatusList") List<OrderStatus> orderStatusList,
	        @Param("paymentStatusList") List<PaymentStatus> paymentStatusList,
	        @Param("paymentMethodList") List<PaymentMethod> paymentMethodList,
			@Param("searchKeyword") String searchKeyword);
	
	@Query("SELECT o FROM Order o "
			+ "WHERE "
			+ "o.orderStatus IN :orderStatusList "
			+ "AND o.paymentStatus IN :paymentStatusList "
			+ "AND o.paymentMethod IN :paymentMethodList ") 
	public Page<Order> getAllOrdersByPagination(
			Pageable pageable,
			@Param("orderStatusList") List<OrderStatus> orderStatusList,
			@Param("paymentStatusList") List<PaymentStatus> paymentStatusList,
			@Param("paymentMethodList") List<PaymentMethod> paymentMethodList);

	// @Query(value = "SELECT "+
	// 		 "DATE(o.orderDate) AS date, "+
	// 		 "COUNT(o.id) AS count, "+
	// 		 "SUM(o.totalPricePaid) AS sum , "+
	// 		 "SUM(op.productQuantity) as quantity "+
	// 		 "FROM Order o "+ 
	// 		 "JOIN o.products op "+
  //           "WHERE "+
  //           //"o.payment_status = 'PAID' "+
	// 		 //"AND "+
	// 		 "MONTH(o.orderDate) = MONTH(CURDATE()) "+
	// 		 "GROUP BY DATE(o.orderDate) "+  
	// 		 "ORDER BY DATE(o.orderDate)")
	@Query(value = "SELECT "+
            "DATE(o.orderDate) AS date, "+
            "COUNT(o.id) AS count, "+
            "SUM(o.totalPricePaid) AS sum , "+
            "SUM(op.productQuantity) as quantity "+
            "FROM Order o "+ 
            "JOIN o.products op "+
            "WHERE "+
            //"o.payment_status = 'PAID' "+
            //"AND "+
            "MONTH(o.orderDate) = MONTH(CURDATE()) "+
            "GROUP BY DATE(o.orderDate), o.orderDate "+  
            "ORDER BY DATE(o.orderDate)")
	public Page<Object[]> getDayOrderAllDetails(Pageable pageable); // for a month by days

	@Query(value = "SELECT "+
			"MONTHNAME(o.orderDate) AS month, "+
			"COUNT(o.id) AS count, "+
			"SUM(o.totalPricePaid) AS sum , "+
			"SUM(op.productQuantity) as quantity "+
			"FROM Order o "+ 
			"JOIN o.products op "+
			"WHERE "+
			//"o.payment_status = 'PAID' "+
			//"AND "+
			"YEAR(o.orderDate) = YEAR(CURDATE()) "+
			"GROUP BY MONTH(o.orderDate) "+  
			"ORDER BY MONTH(o.orderDate)")
	public Page<Object[]> getMonthOrderAllDetails(Pageable pageable); // for a month by days
	@Query(value = "SELECT "+
			"YEAR(o.orderDate) AS year, "+
			"COUNT(o.id) AS count, "+
			"SUM(o.totalPricePaid) AS sum , "+
			"SUM(op.productQuantity) as quantity "+
			"FROM Order o "+ 
			"JOIN o.products op "+
			//"WHERE "+
			//"o.payment_status = 'PAID' "+
			"GROUP BY YEAR(o.orderDate) "+  
			"ORDER BY YEAR(o.orderDate)")
	public Page<Object[]> getYearOrderAllDetails(Pageable pageable); // for a month by days

}
