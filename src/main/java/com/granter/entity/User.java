package com.granter.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;
	private String lastName;
	private String mobileNo;

	@Column(unique = true)
	private String email;

	private String password;

	private String professionType;
	private String nationality;
	
	private String userType;

	private Boolean emailVerified;

}