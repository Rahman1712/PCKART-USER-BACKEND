package com.ar.pckart.order.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "track_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "order_status")
	private OrderStatus order_status;
	
	@Column(name = "description", length = 512)
	private String description;
	
	@Column(name = "status_time")
	private LocalDateTime status_time;
	
}
