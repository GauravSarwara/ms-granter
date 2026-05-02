package com.granter.request;

import lombok.Data;

@Data
public class ApplicantPropertyDetails {
	private String email;
    private String accommodationType;

    private String landlordName;
    private String propertyAddress;

    private String monthlyRent;

    private String tenancyStartDate;

    private String tenancyEndDate;


}
