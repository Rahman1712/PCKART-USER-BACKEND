package com.ar.pckart.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.user.model.Address;

import jakarta.transaction.Transactional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{

	@Modifying
	@Transactional
	@Query("UPDATE Address a SET "
			+ "a.contact = :contact "
			+ "WHERE a.id = :id")
	void updateContactOnlyById(@Param("id")Long id, 
			@Param("contact")String contact);
	
	
	@Modifying
	@Transactional
    @Query("UPDATE Address a SET "
    		+ "a.contact = :contact, a.alternative_contact=:alternative_contact "
    		+ "WHERE a.id = :id")
	void updateContactById(@Param("id")Long id, 
			@Param("contact")String contact, 
			@Param("alternative_contact") String alternative_contact);

}
