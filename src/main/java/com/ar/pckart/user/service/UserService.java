package com.ar.pckart.user.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ar.pckart.user.config.JwtService;
import com.ar.pckart.user.dto.AuthenticationRequest;
import com.ar.pckart.user.dto.AuthenticationResponse;
import com.ar.pckart.user.dto.UserDTO;
import com.ar.pckart.user.dto.UserRegisterRequest;
import com.ar.pckart.user.model.Address;
import com.ar.pckart.user.model.OtpData;
import com.ar.pckart.user.model.Role;
import com.ar.pckart.user.model.Token;
import com.ar.pckart.user.model.TokenType;
import com.ar.pckart.user.model.User;
import com.ar.pckart.user.repo.AddressRepository;
import com.ar.pckart.user.repo.TokenRepository;
import com.ar.pckart.user.repo.UserRepository;
import com.ar.pckart.user.util.ImageUtils;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

@Service
public class UserService {

	@Autowired private UserRepository userRepo;
	@Autowired private TokenRepository tokenRepo;
	@Autowired private JwtService jwtService;
	@Autowired private MailService mailService;
	@Autowired private OTPService otpService;
	@Autowired private MessageJwtService messageJwtService;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private AddressRepository addressRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public AuthenticationResponse register(UserRegisterRequest request) {
		try {
			var user = User.builder()
					.fullname(request.getFullname())
					.email(request.getEmail())
					.mobile(request.getMobile())
					.username(request.getUsername())
					.password(passwordEncoder.encode(request.getPassword()))
					.role(Role.USER)
					.nonLocked(true) // at register not lock true
					.enabled(false) // not enable at start
					.build();
			
			userRepo.findByUsername(request.getUsername()).ifPresent(
					u-> {
//						throw new UserLoginException("username already exists");
						throw new UserRegisterException(new ErrorResponse("username already exists.", "username", request.getUsername()));
					});
			
			User userSaved = userRepo.save(user);
			
			try {
				sendMailForVerify(userSaved.getEmail());
			} catch (UnsupportedEncodingException | MessagingException e) {
				e.printStackTrace();
				throw new UserLoginException("Error in new Registration ...");
			}
			
			var jwtToken = jwtService.generateToken(new UsersDetails(user));
			var refreshToken = jwtService.generateRefreshToken(new UsersDetails(user));
			
			saveUserToken(userSaved, jwtToken);
			
			return AuthenticationResponse.builder()
					//.token(jwtToken)
					.accessToken(jwtToken)
					.refreshToken(refreshToken)
					.build();
		}
		catch (DataIntegrityViolationException | ConstraintViolationException e) {
			String errorMessage = e.getMessage();
		    /*
			could not execute statement [ERROR: duplicate key value violates unique constraint "uk_6dotkott2kjsp8vw4d0m25fb7"
  			Detail: Key (email)=(usman@gmail.com) already exists.] [insert into users (email,enabled,fullname,admin_image,admin_image_name,admin_image_type,mobile,non_locked,password,role,username) values (?,?,?,?,?,?,?,?,?,?,?)]; SQL [insert into users (email,enabled,fullname,admin_image,admin_image_name,admin_image_type,mobile,non_locked,password,role,username) values (?,?,?,?,?,?,?,?,?,?,?)]; constraint [uk_6dotkott2kjsp8vw4d0m25fb7]
		     */
		    
		    int startDetailIndex = errorMessage.indexOf("Detail: Key (") + "Detail: Key (".length();
		    int endDetailIndex = errorMessage.indexOf(")", startDetailIndex);

		    String field = errorMessage.substring(startDetailIndex, endDetailIndex);
		    String fieldValue = field.equals("email") ? request.getEmail() : request.getMobile() ;  // only username,email,mobile are unique username managed in abow , now only email and mobile  

		    //System.err.println(field+ " already exists.");
		    throw new UserRegisterException(new ErrorResponse(field+ " already exists.", field, fieldValue));
		}
		catch (Exception ex) {
			//ex.printStackTrace();
			throw ex;
		}
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						 request.getUsername(),
						 request.getPassword()
						 )
				);
		
		var user = userRepo.findByUsername(request.getUsername())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(new UsersDetails(user));
		var refreshToken = jwtService.generateRefreshToken(new UsersDetails(user));
		
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		
		return AuthenticationResponse.builder()
				//.token(jwtToken)
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.username(user.getUsername())
				.role(user.getRole().name())
				.build();
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = Token.builder()
				.user(user)
				.token(jwtToken)
				.tokenType(TokenType.BEARER)
				.expired(false)
				.revoked(false)
				.build();
		tokenRepo.save(token);
	}
	
	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());
		
		if(validUserTokens.isEmpty()) return;
		
		validUserTokens.forEach(t ->{
			t.setExpired(true);
			t.setRevoked(true);
		});
		
		tokenRepo.saveAll(validUserTokens);
	}

	public UserDTO findByUsername(String username) {
		User user = userRepo.findByUsername(username).get();
		return UserToUserDto(user);
	}
	
	public List<UserDTO> allUsersDtos() {
		List<User> allUsers = userRepo.findAll(); 
		List<UserDTO> allUsersDtos = allUsers.stream().map(user -> {
			/*if(user.getImage()!=null && user.getImage().length>0) {
				user.setImage(ImageUtils.decompress(user.getImage()));
			}*/
			return UserToUserDto(user);
		}).collect(Collectors.toList());
		return allUsersDtos;
	}
	
	public String updateUserById(Long id,MultipartFile file,UserRegisterRequest request) throws IOException {
		userRepo.updateUserById(id,
				request.getFullname(),
				request.getMobile(),
				request.getEmail(),
				ImageUtils.compress(file.getBytes()),
				file.getOriginalFilename(),
				file.getContentType()
				);
		return "User detail updated successfully ... "; 
	}
	
	public String updateEnable(Long id, boolean enabled) {
		userRepo.updateEnabledById(id,enabled);
		return "User detail updated successfully ... ";
	}
	
	public String updateNonLocked(Long id, boolean nonLocked) {
		userRepo.updateNonLockedById(id,nonLocked);
		return "User detail updated successfully ... ";
	}
	

	public UserDTO address(Long id,List<Address> addresses) {
		User findById = userRepo.findById(id).get();
//		findById.setAddressList(Set.of(addresses));
		if(addresses.size() == 1) addresses.get(0).setSelected(true);
		findById.setAddresses(addresses);
		userRepo.save(findById);
		return UserToUserDto(userRepo.findById(id).get());
	}
	

	public List<Address> getAddressesByUserId(Long id) {
		return userRepo.findAddressesById(id);
	}
	
	public String deleteUserById(Long id) {
		userRepo.deleteById(id);
		return "deleted";
	}

	public String deleteAllAddressById(Long id, Long adid) {
		User user = userRepo.findById(adid).get();
		List<Address> collect = user.getAddresses().stream().dropWhile(a -> a.getId() == adid).collect(Collectors.toList());
		user.setAddresses(collect);
		userRepo.save(user);
		return "All addresses deleted ... ";
	}
	
	public String updateAddressContactOnly(Long addressId, String contact) {
		addressRepo.updateContactOnlyById(addressId,contact);
		return "Address Phone updated successfully";
	}
	public String updateAddressContact(Long addressId, String contact, String alternative_contact) {
		addressRepo.updateContactById(addressId,contact, alternative_contact);
		return "Address Phone updated successfully";
	}
	
	public String updateAddressSeleted(Long addressId, Long userId) {
		User user = userRepo.findById(userId).get();
		List<Address> addresses = user.getAddresses();

//		List<Address> addresses = user.getAddresses().stream()
//		        .map(address -> {
//		            if (address.getId().equals(addressId)) {
//		                address.setSelected(true);
//		            } else {
//		                address.setSelected(false);
//		            }
//		            return address;
//		        })
//		        .collect(Collectors.toList());
		for (Address address : addresses) {
		    if (address.getId().equals(addressId)) {
		        address.setSelected(true);
		    } else {
		        address.setSelected(false);
		    }
		}
		user.setAddresses(addresses);
		userRepo.save(user);
		return "Address Selected updated successfully";
	}
	
	public String deleteAddress(Long addressId) {
		addressRepo.deleteById(addressId);
		return "Address deleted successfully";
	}
	
	
	public User getUserById(Long id) {   // to get user
		return userRepo.findById(id).orElse(null);
	}
	public UserDTO findUserById(Long id) {
		return UserToUserDto(userRepo.findById(id).get());
	}
	public UserDTO findUserByEmail(String email) {
		return UserToUserDto(userRepo.findByEmail(email).get());
	}
	private Optional<User> findUserByEmailPrivate(String email) {
		return userRepo.findByEmail(email);
	}
	public Long findIdByUsername(String username) {
		return userRepo.findIdByUsername(username);
	}
	public Long findIdByEmail(String email) {
		return userRepo.findIdByEmail(email);
	}
	
	
//	public void updatePassword(String newPassword, User user) {
//		String encodedPassword = passwordEncoder.encode(newPassword);
//		user.setPassword(encodedPassword);
//		userRepo.save(user);
//	}
	public String updatePasswordById(Long id, String currentPassword, String newPassword) {
		User user = userRepo.findById(id).get();
		if(passwordEncoder.matches(currentPassword, user.getPassword())){
			if(passwordEncoder.matches(newPassword, user.getPassword())) {
				throw new UserLoginException("current password and new password is same change it.");
			}
			userRepo.updatePassword(id,passwordEncoder.encode(newPassword));
		}else {
			throw new UserLoginException("current password doesn't match");
		}
		return "User detail updated successfully ... ";
	}
	
	public String sendMailForVerify(String userEmail) throws UnsupportedEncodingException, MessagingException {
        // Retrieve the OTP data for the user
    	User user = findUserByEmailPrivate(userEmail)
        		.orElseThrow(() -> new RuntimeException("Not Valid Email Id"));
    	
    	String otp = otpService.generateOTP(user);
    	String token = messageJwtService.generateTokenForOtp(userEmail, otp);
    	return mailService.sendTokenMail(user, token);	
	}
	
	public String verifyOTP(String token) {
		String extractEmail = messageJwtService.extractEmail(token);
        User user = findUserByEmailPrivate(extractEmail)
        		.orElseThrow(() -> new RuntimeException("Not Valid Email Id"));
        
        OtpData otpData = otpService.getOtpDataByUserId(user.getId()).get();
        if(messageJwtService.isTokenValid(token, extractEmail, otpData.getOtp())) {
        	userRepo.updateEnabledById(user.getId(), true); //TRUE
        	otpService.deleteOtpData(otpData);
        	return "Verified Successfully";
        }
        else {
        	throw new UserLoginException("Not valid your account : error occured");
        }
	}
	
	public String resetPasswordRequest(String email) throws UnsupportedEncodingException, MessagingException {
		User user = findUserByEmailPrivate(email)
        		.orElseThrow(() -> new UserLoginException("Not Valid Email Id"));
    	
    	String otp = otpService.generateOTP(user);
    	
    	return mailService.sendOTPMail(user, otp);	
	}
	public String updateForgotPasswordByEmail(String email, String newPassword, String otp) {
		User user = findUserByEmailPrivate(email)
        		.orElseThrow(() -> new UserLoginException("Not Valid Email Id"));
		
		if(otpService.verifyOTP(user, otp)) {
			userRepo.updatePassword(user.getId(),passwordEncoder.encode(newPassword));
			userRepo.updateEnabledById(user.getId(), true); //TRUE  : also update to enabled to true
			return "password updated successfully";
		}else {
			throw new UserLoginException("otp is not same");
		}
	}
	
	private UserDTO UserToUserDto(User user) {
		if(user.getImage()!=null && user.getImage().length>0) {
			user.setImage(ImageUtils.decompress(user.getImage()));
		}
		List<Address> addresses = getAddressesByUserId(user.getId());
		List<LocalDateTime> lastFiveLogins = tokenRepo.findLastFiveLoginsByUser(user.getId());
		
		return UserDTO.builder()
				.id(user.getId())
				.fullname(user.getFullname())
				.email(user.getEmail())
				.mobile(user.getMobile())
				.username(user.getUsername())     
				.password(user.getPassword())
				.role(user.getRole().name())
				.image(user.getImage())
				.imageName(user.getImageName())
				.imageType(user.getImageType())
				.enabled(user.isEnabled())        
				.nonLocked(user.isNonLocked())
				.addresses(addresses)
				.last_logins(lastFiveLogins)
				.build();   
	}

	public void refreshToken(
			HttpServletRequest request, 
			HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
		
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userName;
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.split(" ")[1].trim(); //refreshToken = authHeader.substring(7);
		userName = jwtService.extractUsername(refreshToken);
		
		if(userName != null) {
			var user = this.userRepo.findByUsername(userName)
					.orElseThrow();
			
			if(jwtService.isTokenValid(refreshToken, new UsersDetails(user))) {
				var accessToken = jwtService.generateToken(new UsersDetails(user));
				
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				
				var authResponse = AuthenticationResponse.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}

//	public void deleteAddress(Long id, Address address) {
//		addressRepo.delete(address);
//	}
	
/*============================ COUNT ======================================================*/
	public Long getCountOfUsers() {
		return userRepo.count();
	}


}
