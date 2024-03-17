package com.ar.pckart.payment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ar.pckart.payment.model.Wallet;
import com.ar.pckart.user.model.User;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

    public Wallet findByUser(User user);

    @Query("SELECT w.balance FROM Wallet w WHERE w.user = :user")
    public Double findBalanceByUser(User user);
}
