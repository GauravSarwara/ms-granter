package com.granter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;
	private String middleName;
	private String lastName;
	private String mobileNo;

	@Column(unique = true)
	private String email;

	private String password;

	private String professionType;
	
	private String nationality;
	
	private String userType;
	
	private String emailVerificationCode;
	private String mobileVerificationCode;

	private Boolean emailVerified;

}