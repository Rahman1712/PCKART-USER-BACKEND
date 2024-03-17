package com.ar.pckart.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ar.pckart.user.model.Address;
import com.ar.pckart.user.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "SELECT u.id FROM User u WHERE u.username = ?1")
	public Long findIdByUsername(String username);
	
	@Query(value = "SELECT u.id FROM User u WHERE u.email = ?1")
	public Long findIdByEmail(String email);
	
	public Optional<User> findByUsername(String username);

	public Optional<User> findByEmail(String userEmail);

	@Query(value = "SELECT u.addresses FROM User u WHERE u.id = ?1")
	public List<Address> findAddressesById(Long id);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET "
			+ "u.enabled = :enabled "
			+ "WHERE u.id = :id")
	void updateEnabledById(@Param("id")Long id, 
			@Param("enabled")boolean enabled);
	
	@Modifying
	@Transactional
	@Query("UPDATE User u SET "
			+ "u.nonLocked = :nonLocked "
			+ "WHERE u.id = :id")
	void updateNonLockedById(@Param("id")Long id, 
			@Param("nonLocked")boolean nonLocked);
	
	@Modifying
	@Transactional
	@Query("UPDATE User u SET "
			+ "u.password = :newPassword "
			+ "WHERE u.id = :id")
	void updatePassword(@Param("id")Long id, @Param("newPassword")String newPassword);


	
	@Modifying
	@Transactional
	@Query("update User u set u.fullname = :fullname, "
			+ "u.mobile= :mobile, u.email= :email, "
			+ "u.image = :image, "
			+ "u.imageName = :imageName, "
			+ "u.imageType = :imageType "
			+ "where u.id = :id")
	void updateUserById(@Param("id")Long id,@Param("fullname") String fullname,
			@Param("mobile")String mobile, @Param("email")String email, 
			@Param("image") byte[] image,
			@Param("imageName")String imageName,
			@Param("imageType")String imageType);

	
}

