package com.ar.pckart.user.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ar.pckart.user.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>{

	@Query("""
		SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id
		WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)
	""")
	List<Token> findAllValidTokensByUser(Long userId);
	
	Optional<Token> findByToken(String token);
	
	@Query("""
		SELECT t.logged_at FROM Token t INNER JOIN User u ON t.user.id = u.id
		WHERE u.id = :userId ORDER BY t.logged_at DESC LIMIT 5
	""")
	List<LocalDateTime> findLastFiveLoginsByUser(Long userId);
}
