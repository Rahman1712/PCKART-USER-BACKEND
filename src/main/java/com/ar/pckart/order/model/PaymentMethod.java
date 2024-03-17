package com.ar.pckart.order.model;

public enum PaymentMethod {
    
	ONLINE("Online"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    WALLET("Wallet Payment");
    
    private final String status;
    
    private PaymentMethod(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
}
