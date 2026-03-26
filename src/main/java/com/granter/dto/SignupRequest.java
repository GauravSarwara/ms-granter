package com.granter.dto;

import lombok.Data;

@Data
public class SignupRequest {

	private String firstName;
	private String lastName;
	private String mobileNo;
	private String email;
	private String password;
	private String professionType;
	private String nationality;

}