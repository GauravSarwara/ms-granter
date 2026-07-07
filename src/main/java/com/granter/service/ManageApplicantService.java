package com.granter.service;

import org.springframework.http.ResponseEntity;

import com.granter.request.ApplicantPropertyDetails;
import com.granter.request.ApplicationDetail;
import com.granter.request.VerificationCompletedWebhook;
public interface ManageApplicantService {

	ResponseEntity<Object> getUserDetailByEmail(String email,Boolean active);

	ResponseEntity<Object> createApplicationDetail(ApplicationDetail userDetail);

	ResponseEntity<Object> createUserPropertyDetail(ApplicantPropertyDetails applicantPropertyDetails);

	ResponseEntity<Object> getVerifyKonfir(String email);

	ResponseEntity<Object> konfirVerified(Long candidateid, Boolean verified);

	ResponseEntity<Object> konfirVerifiedByWebHook(VerificationCompletedWebhook request);

}
