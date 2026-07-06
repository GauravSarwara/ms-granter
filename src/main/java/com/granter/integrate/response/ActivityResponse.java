package com.granter.integrate.response;

import lombok.Data;

@Data
public class ActivityResponse {

	private String id;
	private String customId;
	private String type;
	private String status;
	private String startDate;
	private String endDate;
	private Boolean isCurrent;
	private Boolean verifyViaKonfir;

	private String employerId;
	private String employerName;
	private String jobTitle;
	private String declaredAnnualIncome;
	private String institution;
}
