package com.ar.pckart.order.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.uuid.UuidGenerator;

import com.ar.pckart.coupon.model.UserCoupon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
//	@GeneratedValue( generator = "uuid2", strategy = GenerationType.UUID)
	@GeneratedValue( strategy = GenerationType.UUID)
//	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@GenericGenerator(name = "uuid", type = UuidGenerator.class)
	private String id;
	
	@Column(name = "user_id" , nullable = false)
    private Long userId;
	
	@OneToOne(fetch = FetchType.EAGER,
    		targetEntity = OrderAddress.class, 
    		cascade = CascadeType.ALL
    		)  
	@JoinColumn(name = "order_address_id", referencedColumnName = "id")
    private OrderAddress orderAddress;  
	
    @OneToMany(fetch = FetchType.EAGER, 
    		targetEntity = OrderProduct.class, 
    		cascade = CascadeType.ALL)
    @JoinColumn(name = "pl_fk", referencedColumnName = "id")
    private List<OrderProduct> products;  
    
    @Column(name = "grand_total_price")
    private Double grandTotalPrice;
     
    @Column(name = "shipping_charge")
    private float shippingCharge;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;
    
    @Column(name = "coupon_discount")
    private float couponDiscount;
    
	@Column(name = "total_price_paid")
	private Double totalPricePaid;
	
	@OneToMany(fetch = FetchType.EAGER, 
			targetEntity = TrackStatus.class, 
			cascade = CascadeType.ALL)
	@JoinColumn(name = "ts_fk", referencedColumnName = "id")
	private List<TrackStatus> trackStatus;
	
	@Column(name = "tracking_no", nullable = false)
	private String trackingNo;
	
	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus;
	
	@Column(name = "payment_id")
	private String paymentId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;
	 
}
