package com.granter.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.granter.request.ApplicationDetail;
import com.granter.service.ManageApplicantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ApplicantController {

	private final ManageApplicantService manageApplicantService;

	@GetMapping("/detail")
	public ResponseEntity<Object> getUserDetail(@RequestParam String email, @RequestParam Boolean active
	) {
		return manageApplicantService.getUserDetailByEmail(email, active);
	}
	
	@PostMapping("/detail")
	public ResponseEntity<Object> updateUserDetail(@RequestBody ApplicationDetail userDetail) {

		return manageApplicantService.createApplicationDetail(userDetail);

	}
	

}