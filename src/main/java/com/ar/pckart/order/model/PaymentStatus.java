package com.ar.pckart.order.model;

public enum PaymentStatus {
    
	PENDING("Pending"),
    PAID("Paid"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");
    
    private final String status;
    
    private PaymentStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
}
