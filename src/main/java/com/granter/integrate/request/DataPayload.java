package com.granter.integrate.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayload {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String nationalInsuranceNumber;
    private String dateOfBirth;
    private String email;

    private String newEmployerName;
    private String newJobTitle;
    private Boolean consentToContactCurrentEmployer;

    private List<Activity> activities;
}