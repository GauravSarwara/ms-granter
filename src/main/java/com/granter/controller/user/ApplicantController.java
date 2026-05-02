package com.granter.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.granter.constant.UrlConstant;
import com.granter.request.ApplicantPropertyDetails;
import com.granter.request.ApplicationDetail;
import com.granter.service.ManageApplicantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(UrlConstant.USER_BASE_URL)
@RequiredArgsConstructor
public class ApplicantController {

	private final ManageApplicantService manageApplicantService;

	@GetMapping(UrlConstant.USER_DETAIL)
	public ResponseEntity<Object> getUserDetail(@RequestParam
			(required = true)String email, @RequestParam(required = true) Boolean active
	) {
		return manageApplicantService.getUserDetailByEmail(email, active);
	}
	
	@PostMapping(UrlConstant.USER_DETAIL)
	public ResponseEntity<Object> updateUserDetail(@RequestBody ApplicationDetail userDetail) {

		return manageApplicantService.createApplicationDetail(userDetail);

	}
	
	@PostMapping(UrlConstant.PROPERTY_DETAIL)
	public ResponseEntity<Object> createUserPropertyDetail(@RequestBody ApplicantPropertyDetails applicantPropertyDetails) {

		return manageApplicantService.createUserPropertyDetail(applicantPropertyDetails);

	}
	

}