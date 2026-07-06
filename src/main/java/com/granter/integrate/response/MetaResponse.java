package com.granter.integrate.response;

import lombok.Data;

@Data
public class MetaResponse {

	private String redirectUrl;
	private String webhookUrl;
	private String candidateUrl;
	private Boolean isSandbox;
}