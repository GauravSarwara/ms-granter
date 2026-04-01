package com.granter.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.granter.dto.GeneraicResponse;
import com.granter.entity.GranterApplication;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.UserRepository;
import com.granter.service.ManageApplicantService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageApplicantServiceImpl implements ManageApplicantService {

	private UserRepository userRepository;
	private GranterApplicationRepository applicationRepository;
	
	@Override
	public ResponseEntity<Object> getUserDetailByEmail(String email) {
		GeneraicResponse generaicResponse=new GeneraicResponse();
		
		try{
			var user=userRepository.findByEmail(email);
			List<GranterApplication> applicantDetail=applicationRepository.findByUser(user);
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
