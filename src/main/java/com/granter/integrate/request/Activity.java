package com.granter.integrate.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private String type;
    private Boolean isCurrent;
    private String startDate;
    private String endDate;
    private Boolean verifyViaKonfir;

    // Self Employment
    private String customId;
    private String uniqueTaxpayerReference;
    private String companyName;
    private String companyNumber;
    private String accountingProvider;
    private String accountingAccess;
    private String notes;

    // Employment
    private String employerId;
    private String employerName;
    private String jobTitle;
    private BigDecimal declaredAnnualIncome;

    // Education
    private String institution;
}