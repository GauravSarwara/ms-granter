package com.granter.integrate.response;

import java.util.List;

import lombok.Data;

@Data
public class DataResponse {

	private String id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String dateOfBirth;
	private String email;
	private String status;
	private String newEmployerName;
	private List<ActivityResponse> activities;
}