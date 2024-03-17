package com.ar.pckart.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ar.pckart.payment.model.Payment;
import com.ar.pckart.payment.model.RazorPayment;

@Repository
public interface RazorPaymentRepository extends JpaRepository<RazorPayment, String>{}

//@Repository
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//}
