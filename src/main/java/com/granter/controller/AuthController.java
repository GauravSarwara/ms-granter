package com.granter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.granter.dto.JwtResponse;
import com.granter.dto.LoginRequest;
import com.granter.dto.SignupRequest;
import com.granter.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody SignupRequest request) {

		log.info("API signup called");
		return userService.signup(request);
		
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginRequest request) {

		log.info("API login called");

		return userService.login(request);
	}
	
	@GetMapping("/verify")
	public ResponseEntity<Object> verifyUser(@RequestParam String token) {
		return userService.verifyAccount(token);	    
	}

}