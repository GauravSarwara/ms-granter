package com.granter.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDetails {

	private String userNumber;	
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String email;
	private String professionType;
	private String nationality;
	private List<ApplicantDetail> appData;

	
}
