package com.granter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.granter.constant.UrlConstant;
import com.granter.dto.LoginRequest;
import com.granter.dto.SignupRequest;
import com.granter.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(UrlConstant.AUTH_BASE_URL)
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	
	@PostMapping(UrlConstant.AUTH_SIGNUP)
	public ResponseEntity<Object> signup(@RequestBody SignupRequest request) {

		log.info("API signup called");
		return userService.signup(request);
		
	}

	@PostMapping(UrlConstant.AUTH_LOGIN)
	public ResponseEntity<Object> login(@RequestBody LoginRequest request) {

		log.info("API login called");

		return userService.login(request);
	}
	
	@GetMapping(UrlConstant.AUTH_VERIFY)
	public ResponseEntity<Object> verifyUser(@RequestParam(required = true) String emailToken,
			@RequestParam(required = true) String email,
			@RequestParam(required = true) String phoneNoToken
			
			) {
		return userService.verifyAccount(emailToken,email,phoneNoToken);	    
	}
	
	@PostMapping("/webhook")
	public ResponseEntity<Object> webHook(@RequestBody String request) {

		log.info("API login called");
		System.out.println(request);
		return ResponseEntity.ok("success");
	}

}