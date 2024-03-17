package com.ar.pckart.payment.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ar.pckart.payment.model.WalletHistory;
import com.ar.pckart.user.model.User;

import java.util.List;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory, String> {
	
	public List<WalletHistory> findAllByUser(User user);
	
	public Page<WalletHistory> findAllByUser(User user, Pageable pageable);
}
