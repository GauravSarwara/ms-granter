package com.granter.service.impl;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.granter.dto.JwtResponse;
import com.granter.dto.LoginRequest;
import com.granter.dto.SignupRequest;
import com.granter.entity.ApplicationProfession;
import com.granter.entity.GranterApplication;
import com.granter.entity.Profession;
import com.granter.entity.User;
import com.granter.repository.ApplicationProfessionRepository;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.ProfessionRepository;
import com.granter.repository.UserRepository;
import com.granter.response.GenericResponsePojo;
import com.granter.security.JwtUtil;
import com.granter.service.UserService;
import com.granter.utility.SendMessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final SendMessageUtil sendMessageUtil;
	private final GranterApplicationRepository granterApplicationRepository;
	private final ProfessionRepository professionRepository;
	private final ApplicationProfessionRepository applicationProfessionRepository;
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private static final SecureRandom random = new SecureRandom();

	@Override
	public ResponseEntity<Object> signup(SignupRequest request) {

		
		log.info("Signup started {}", request.getEmail());

		var userData=userRepository.findByEmail(request.getEmail());
		if(userData!=null && userData.getEmail()!=null) {
			var response=GenericResponsePojo.failure("User with this email already exists. ","");			
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if(userData!=null && userData.getMobileNo()!=null) {
			var response=GenericResponsePojo.failure("User with this Mobile number already exists. ","");			
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		User user = new User();

		user.setFirstName(request.getFirstName());
		user.setMiddleName(request.getMiddleName());
		user.setLastName(request.getLastName());
		user.setMobileNo(request.getMobileNo());
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setProfessionType(request.getProfessionType());
		user.setNationality(request.getNationality());
		user.setEmailVerified(false);
		user.setUserType("user");
		String mobileOTP=generateOtp();
		String mailOTP=generateOtp();
		user.setEmailVerificationCode(mailOTP);
		user.setMobileVerificationCode(mobileOTP);
		userRepository.save(user);			
		
		sendMessageUtil.sendVerificationEmail(request.getEmail(),mailOTP);
		// need to send the OTP for phone number
		sendMessageUtil.sendSms(request.getMobileNo(), "Your verifcation code is: "+mobileOTP);
		
		log.info("User saved {}", request.getEmail());
		Map<String,String> data=new HashMap<>();
		data.put("email", request.getEmail());
		data.put("name", request.getFirstName()+" "+request.getLastName());
		var response=GenericResponsePojo.success(data, "signup successfully.Otp sent on email and phone number.");
		
		return  ResponseEntity.ok(response);

	}

	 
	    public String generateOtp() {
	        return String.valueOf(100000 + random.nextInt(900000));
	    }
	@Override
	public ResponseEntity<Object> login(LoginRequest request) {

		log.info("Login request {}", request.getEmail());

		User user = userRepository.findByEmail(request.getEmail());
		if (!user.getEmailVerified()) {
			var response = new JwtResponse("Please verify your account .Please check you email box", "", "401");

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}


		if (!encoder.matches(request.getPassword(), user.getPassword())) {
			var response = new JwtResponse("Invalid user detail", "", "401");

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
		
		String token = jwtUtil.generateToken(user.getEmail());
		var response = new JwtResponse("Login successfully ", token, "200");

		return ResponseEntity.ok(response);

	}

	@Override
	public ResponseEntity<Object> verifyAccount(String emailtoken, String email, String phoneNoToken) {
		User user = userRepository.findByEmail(email);
		if (user != null && user.getEmailVerified()) {
			var response = GenericResponsePojo.success("", "User already verified.");
			return ResponseEntity.ok(response);
		}
		if (user != null && user.getEmailVerificationCode().equals(emailtoken)
				&& user.getMobileVerificationCode().equals(phoneNoToken)) {
			user.setEmailVerified(true);
			userRepository.save(user);

			GranterApplication application = new GranterApplication();
			application.setUserId(user.getId());
			application.setStatus(true);
			application.setCreatedAt(OffsetDateTime.now());
			application.setStep("0");
			application.setCreatedBy(user.getId() + "");
			granterApplicationRepository.save(application);

			Set<String> professionNames = Arrays.stream(user.getProfessionType().split(",")).map(String::trim)
					.filter(s -> !s.isEmpty()).map(String::toUpperCase).collect(Collectors.toSet());
			List<Profession> professions = professionRepository.findByNameIn(professionNames);

			if (professions.size() == professionNames.size()) {
				List<ApplicationProfession> mappings = professions.stream().map(profession -> ApplicationProfession
						.builder().application(application).profession(profession).build()).toList();
				applicationProfessionRepository.saveAll(mappings);
			}
		}else {
			
		}
		var response = GenericResponsePojo.success("", "User verified successfully.");
		return ResponseEntity.ok(response);
	}

}