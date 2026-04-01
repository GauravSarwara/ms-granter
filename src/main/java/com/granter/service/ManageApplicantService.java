package com.granter.service;

import org.springframework.http.ResponseEntity;

public interface ManageApplicantService {

	ResponseEntity<Object> getUserDetailByEmail(String email);

}
