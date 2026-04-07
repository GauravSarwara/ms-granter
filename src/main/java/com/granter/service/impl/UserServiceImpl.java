package com.granter.service.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.granter.dto.JwtResponse;
import com.granter.dto.LoginRequest;
import com.granter.dto.SignupRequest;
import com.granter.entity.GranterApplication;
import com.granter.entity.User;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.UserRepository;
import com.granter.response.GenericResponsePojo;
import com.granter.security.JwtUtil;
import com.granter.service.UserService;
import com.granter.utility.EmailUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final EmailUtil emailUtil;
	private final GranterApplicationRepository granterApplicationRepository;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public ResponseEntity<Object> signup(SignupRequest request) {

		log.info("Signup started {}", request.getEmail());

		var userData=userRepository.findByEmail(request.getEmail());
		if(userData!=null && userData.getEmail()!=null) {
			var response=GenericResponsePojo.failure("User with this email already exists. ","");			
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		User user = new User();

		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setMobileNo(request.getMobileNo());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setProfessionType(request.getProfessionType());
		user.setNationality(request.getNationality());
		user.setEmailVerified(false);
		user.setUserType("user");
		userRepository.save(user);
		
		emailUtil.sendVerificationEmail(request.getEmail(),  Base64.getUrlEncoder().encodeToString(request.getEmail().getBytes()));
		log.info("User saved {}", request.getEmail());
		Map<String,String> data=new HashMap<>();
		data.put("email", request.getEmail());
		data.put("name", request.getFirstName()+" "+request.getLastName());
		var response=GenericResponsePojo.success(data, "signup successfully.");
		
		return  ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<Object> login(LoginRequest request) {

		log.info("Login request {}", request.getEmail());

		User user = userRepository.findByEmail(request.getEmail());

		if (!encoder.matches(request.getPassword(), user.getPassword())) {
			var response = new JwtResponse("Invalid user detail", "", "401");

			ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		if (!user.getEmailVerified()) {
			var response = new JwtResponse("Please verify your account .Please check you email box", "", "401");

			ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		String token = jwtUtil.generateToken(user.getEmail());
		var response = new JwtResponse("Login successfully ", token, "200");

		return ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<Object> verifyAccount(String token) {
		String email = new String(Base64.getUrlDecoder().decode(token));
		User user=userRepository.findByEmail(email);
		if(user!=null && !user.getEmailVerified()) {
			user.setEmailVerified(true);
			userRepository.save(user);
			GranterApplication application=new GranterApplication(); 
			application.setUser(user);
			application.setStatus(true);
			application.setStep("0");
			granterApplicationRepository.save(application);
		}
		var response=GenericResponsePojo.success("", "User verified successfully.");		
		return ResponseEntity.ok(response);
	}

}