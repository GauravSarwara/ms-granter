package com.granter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.granter.dto.ApplicantDetail;
import com.granter.dto.GeneraicResponse;
import com.granter.dto.UserDetails;
import com.granter.entity.GranterApplication;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.UserRepository;
import com.granter.request.ApplicationDetail;
import com.granter.service.ManageApplicantService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ManageApplicantServiceImpl implements ManageApplicantService {

	private final UserRepository userRepository;
	private final GranterApplicationRepository applicationRepository;
	
	@Override
	public ResponseEntity<Object> getUserDetailByEmail(String email,Boolean active) {
		GeneraicResponse generaicResponse = new GeneraicResponse();
		UserDetails userDetails=new UserDetails();
		try {
			var user = userRepository.findByEmail(email);
			userDetails.setEmail(email);
			userDetails.setFirstName(user.getFirstName());
			userDetails.setLastName(user.getLastName());
			userDetails.setMobileNo(user.getMobileNo());
			userDetails.setNationality(user.getNationality());
			userDetails.setProfessionType(user.getProfessionType());
			List<GranterApplication> applicantDetail = applicationRepository.findByUser(user);
			List<ApplicantDetail> listOfApplicantDetails=new ArrayList<>();
			if(active!=null && active) {
				
				var application=applicantDetail.stream().filter(li->li.getStatus()).findAny().orElse(null);
				listOfApplicantDetails.add(filUserDetails(application));				
			}else {
				for (GranterApplication granterApplication : applicantDetail) {
					listOfApplicantDetails.add(filUserDetails(granterApplication));			
				}
			}
			userDetails.setAppData(listOfApplicantDetails);
			generaicResponse.setData(userDetails);
			generaicResponse.setMessage("Records fetched successfully.");
			generaicResponse.setSuccess("true");
			return ResponseEntity.ok(generaicResponse);
		} catch (Exception e) {
			e.printStackTrace();
			generaicResponse.setMessage("An error occurred");
			return ResponseEntity.internalServerError().body(generaicResponse);
		}
	}

	private ApplicantDetail filUserDetails(GranterApplication application) {
		ApplicantDetail applicantData=new ApplicantDetail();
		applicantData.setAddress(application.getAddress());
		applicantData.setDateOfBirth(application.getDateOfBirth());
		applicantData.setEmployerName(application.getEmployerName());
		applicantData.setStatus(application.getStatus());
		applicantData.setStep(application.getStep());
		applicantData.setUniversity(application.getUniversity());
		applicantData.setMonthlyIncome(application.getMonthlyIncome());
		return applicantData;
	}

	@Override
	public ResponseEntity<Object> createApplicationDetail(ApplicationDetail userDetail) {
		GeneraicResponse generaicResponse = new GeneraicResponse();
		try {
			var user = userRepository.findByEmail(userDetail.getEmail());
			List<GranterApplication> applicantDetail = applicationRepository.findByUser(user);

			var application = applicantDetail.stream().filter(li -> li.getStatus()).findAny().orElse(null);
			if (application != null) {
				application.setAddress(userDetail.getAddress());
				application.setDateOfBirth(userDetail.getDateOfBirth());
				application.setEmployerName(userDetail.getEmployerName());
				application.setMonthlyIncome(userDetail.getMonthlyIncome());
				application.setUniversity(userDetail.getUniversity());
				application.setStep("1");
				applicationRepository.save(application);
				generaicResponse.setMessage("Records created successfully.");
				generaicResponse.setSuccess("true");
				return ResponseEntity.ok(generaicResponse);
			} else {
				generaicResponse.setMessage("Records not found.");
				generaicResponse.setSuccess("false");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generaicResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			generaicResponse.setMessage("An error occurred");
			return ResponseEntity.internalServerError().body(generaicResponse);
		}
	}
}


