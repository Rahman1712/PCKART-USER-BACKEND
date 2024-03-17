package com.ar.pckart.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ar.pckart.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>{}

//@Repository
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//}
