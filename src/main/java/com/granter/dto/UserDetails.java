package com.granter.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDetails {

	private String userNumber;	
	private String firstName;
	private String middleName;
	private String lastName;
	private String mobileNo;
	private String email;
	private String professionType;
	private String nationality;
	private String step;
	private List<ApplicantDetail> appData;

	
}
