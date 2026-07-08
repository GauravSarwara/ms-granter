package com.granter.service;

import org.springframework.http.ResponseEntity;

import com.granter.dto.LoginRequest;
import com.granter.dto.SignupRequest;

public interface UserService {

	ResponseEntity<Object> signup(SignupRequest request);

	ResponseEntity<Object> login(LoginRequest request);

	ResponseEntity<Object> verifyAccount(String emailToken,String email,String phoneNoToken);

	ResponseEntity<Object> forgotPasswordByEmail(String email);

	ResponseEntity<Object> passwordUpdate(String otp, String email);

}	