package com.ar.pckart.order.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.order.model.Order;
import com.ar.pckart.order.model.OrderProduct;
import com.ar.pckart.order.model.OrderStatus;
import com.ar.pckart.order.model.PaymentStatus;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>{

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.orderStatus = :orderStatus WHERE o.id = :id")
	void updateOrderStatusById(@Param("id")String id,@Param("orderStatus") OrderStatus orderStatus);

	public List<Order> findAllByUserId(Long userId);
	
	
	public Optional<Order> findByTrackingNo(String trackingNo);

	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.paymentStatus = :paymentStatus WHERE o.id = :id")
	void updatePaymentStatusById(String id, PaymentStatus paymentStatus);

	//List<Order> findTopNByOrderByOrderDateDesc();
	List<Order> findTop10ByOrderByOrderDateDesc();
	List<Order> findTop25ByOrderByOrderDateDesc();
	List<Order> findTop50ByOrderByOrderDateDesc();
	List<Order> findTop100ByOrderByOrderDateDesc();

	List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

	@Query("SELECT SUM(o.totalPricePaid) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double findTotalSumOfPaidTotalPrice();
	
	@Query(value = "SELECT DAY(order_date) AS day, SUM(total_price_paid) AS sum "+
			 "FROM orders "+ 
             "WHERE "+
             "payment_status = 'PAID' "+
			 "AND "+
			 "MONTH(order_date) = MONTH(CURDATE()) "+
			 "GROUP BY DAY(order_date) "+   //"GROUP BY DAYNAME(order_date) "+
			 "ORDER BY DAY(order_date)", nativeQuery = true)
	List<Object[]> getDayOrderTotals(); // for a month by days
	
	@Query(value = "SELECT DAYNAME(order_date) AS day, SUM(total_price_paid) AS sum "+
			"FROM orders "+ 
			"WHERE "+
			"payment_status = 'PAID' "+
			"AND "+
			"WEEK(order_date) = WEEK(CURDATE()) "+
			"GROUP BY DAYNAME(order_date) "+
			"ORDER BY DAY(order_date) ASC", nativeQuery = true)
	List<Object[]> getWeekOrderTotals(); // for a week by day
	
	@Query(value = "SELECT MONTHNAME(order_date) AS month, SUM(total_price_paid) AS sum "+
			"FROM orders "+ 
			"WHERE payment_status = 'PAID' "+ 
			"AND YEAR(order_date) = YEAR(CURDATE()) "+
			"GROUP BY MONTH(order_date) "+
			"ORDER BY MONTH(order_date)", nativeQuery = true)
	List<Object[]> getMonthOrderTotals(); // for a year by months
	
	@Query(value = "SELECT YEAR(order_date) AS year, SUM(total_price_paid) AS sum "+
			"FROM orders "+ 
			"WHERE payment_status = 'PAID' "+
			"GROUP BY YEAR(order_date) "+
			"ORDER BY YEAR(order_date)", nativeQuery = true)
	List<Object[]> getYearOrderTotals();  // for years
	
//	@Query(value = "SELECT o.products "+
//			"FROM Order o "+ 
//			"WHERE "+
//			"o.paymentStatus = 'PAID' "
//			)
	@Query(value = "SELECT "+
			   //"op.* " +
			   "op.product_id as id,op.product_name as name,"+
			   "avg(op.product_price) as price,"+
			   "sum(op.product_quantity) as quantity,"+
			   "sum(op.product_price*op.product_quantity) as revenue " +
		       "FROM orders o " +
		       "JOIN order_products op ON o.id = op.pl_fk "+
		       "WHERE o.payment_status = 'PAID' " +
		       "GROUP BY op.product_id, op.product_name " +
		       "ORDER BY sum(op.product_quantity) DESC LIMIT 10",
		       nativeQuery = true)
	List<Object[]> getMostSellProducts();    
	
	@Query(value = "SELECT "+
			   //"op.* " +
			   "op.product_id as id,op.product_name as name,"+
			   "sum(op.product_quantity) as quantity "+
		       "FROM orders o " +
		       "JOIN order_products op ON o.id = op.pl_fk "+
		       //"WHERE o.id = op.pl_fk " +
		       "GROUP BY op.product_id " +
		       "ORDER BY (SELECT SUM(op.product_quantity)) DESC LIMIT :limit",
		       nativeQuery = true)
	List<Object[]> getMostOrderProductsQuantity(@Param("limit")Long limit); 
	
	
	@Query(value = "SELECT o.orderStatus, COUNT(o) "+
			"FROM Order o "+ 
			"GROUP BY o.orderStatus ")
	List<Object[]> getOrderStatusCounts(); 
	
	
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
			 "GROUP BY DATE(o.orderDate) "+  
			 "ORDER BY DATE(o.orderDate)")
	List<Object[]> getDayOrderAllDetails(); // for a month by days
	
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
	List<Object[]> getMonthOrderAllDetails(); // for monthly
	
	@Query(value = "SELECT "+
			"YEAR(o.orderDate) AS date, "+
			"COUNT(o.id) AS count, "+
			"SUM(o.totalPricePaid) AS sum , "+
			"SUM(op.productQuantity) as quantity "+
			"FROM Order o "+ 
			"JOIN o.products op "+
			//"WHERE "+
			//"o.payment_status = 'PAID' "+
			//"AND "+
			"GROUP BY YEAR(o.orderDate) "+  
			"ORDER BY YEAR(o.orderDate)")
	List<Object[]> getYearOrderAllDetails(); // for year wise
}




//@Query(value = "SELECT "+
//		 "DATE(o.order_date) AS date, "+
//		 "COUNT(o.id) AS count, "+
//		 "SUM(o.total_price_paid) AS sum , "+
//		 "SUM(op.product_quantity) as quantity "+
//		 "FROM orders o "+ 
//		 "JOIN order_products op ON o.id = op.pl_fk "+
//        "WHERE "+
//        //"o.payment_status = 'PAID' "+
//		 //"AND "+
//		 "MONTH(o.order_date) = MONTH(CURDATE()) "+
//		 "GROUP BY DATE(o.order_date) "+  
//		 "ORDER BY DATE(o.order_date)", nativeQuery = true)
//List<Object[]> getDayOrderAllDetails(); // for a month by days

