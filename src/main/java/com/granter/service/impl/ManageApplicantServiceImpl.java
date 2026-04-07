package com.granter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.granter.dto.ApplicantDetail;
import com.granter.dto.GeneraicResponse;
import com.granter.dto.UserDetails;
import com.granter.entity.GranterApplication;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.UserRepository;
import com.granter.request.ApplicationDetail;
import com.granter.service.ManageApplicantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageApplicantServiceImpl implements ManageApplicantService {

    private final UserRepository userRepository;
    private final GranterApplicationRepository applicationRepository;

    @Override
    public ResponseEntity<Object> getUserDetailByEmail(String email, Boolean active) {

        GeneraicResponse generaicResponse = new GeneraicResponse();
        UserDetails userDetails = new UserDetails();

        try {
            log.info("Fetching user details for email: {}", email);

            var user = userRepository.findByEmail(email);

            // ✅ Null check for user
            if (user == null) {
                log.warn("User not found for email: {}", email);
                generaicResponse.setMessage("User not found");
                generaicResponse.setSuccess("false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
            }

            userDetails.setEmail(email);
            userDetails.setFirstName(user.getFirstName());
            userDetails.setLastName(user.getLastName());
            userDetails.setMobileNo(user.getMobileNo());
            userDetails.setNationality(user.getNationality());
            userDetails.setProfessionType(user.getProfessionType());

            List<GranterApplication> applicantDetail = applicationRepository.findByUser(user);

            // ✅ Handle null or empty list
            if (applicantDetail == null || applicantDetail.isEmpty()) {
                log.warn("No applications found for user: {}", email);
                userDetails.setAppData(new ArrayList<>());
            } else {

                List<ApplicantDetail> listOfApplicantDetails = new ArrayList<>();

                if (Boolean.TRUE.equals(active)) {
                    log.info("Fetching only active application for user: {}", email);

                    var application = applicantDetail.stream()
                            .filter(li -> Boolean.TRUE.equals(li.getStatus()))
                            .findAny()
                            .orElse(null);

                    if (application != null) {
                        listOfApplicantDetails.add(filUserDetails(application));
                    } else {
                        log.warn("No active application found for user: {}", email);
                    }

                } else {
                    log.info("Fetching all applications for user: {}", email);

                    for (GranterApplication granterApplication : applicantDetail) {
                        if (granterApplication != null) {
                            listOfApplicantDetails.add(filUserDetails(granterApplication));
                        }
                    }
                }

                userDetails.setAppData(listOfApplicantDetails);
            }

            generaicResponse.setData(userDetails);
            generaicResponse.setMessage("Records fetched successfully.");
            generaicResponse.setSuccess("true");

            return ResponseEntity.ok(generaicResponse);

        } catch (Exception e) {
            log.error("Error while fetching user details for email: {}", email, e);
            generaicResponse.setMessage("An error occurred");
            generaicResponse.setSuccess("false");
            return ResponseEntity.internalServerError().body(generaicResponse);
        }
    }

    private ApplicantDetail filUserDetails(GranterApplication application) {

        ApplicantDetail applicantData = new ApplicantDetail();

        if (application == null) {
            log.warn("Application is null while mapping");
            return applicantData;
        }

        applicantData.setAddress(application.getAddress());
        applicantData.setDateOfBirth(application.getDateOfBirth());
        applicantData.setEmployerName(application.getEmployerName());
        applicantData.setStatus(application.getStatus());
        applicantData.setStep(application.getStep());
        applicantData.setUniversity(application.getUniversity());
        applicantData.setMonthlyIncome(application.getMonthlyIncome());

        return applicantData;
    }

    @Override
    public ResponseEntity<Object> createApplicationDetail(ApplicationDetail userDetail) {

        GeneraicResponse generaicResponse = new GeneraicResponse();

        try {
            log.info("Creating/updating application for email: {}", userDetail.getEmail());

            if (userDetail.getEmail() == null || userDetail.getEmail().isEmpty()) {
                log.warn("Email is missing in request");
                generaicResponse.setMessage("Email is required");
                generaicResponse.setSuccess("false");
                return ResponseEntity.badRequest().body(generaicResponse);
            }

            var user = userRepository.findByEmail(userDetail.getEmail());

            // ✅ Null check
            if (user == null) {
                log.warn("User not found for email: {}", userDetail.getEmail());
                generaicResponse.setMessage("User not found");
                generaicResponse.setSuccess("false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
            }

            List<GranterApplication> applicantDetail = applicationRepository.findByUser(user);

            if (applicantDetail == null || applicantDetail.isEmpty()) {
                log.warn("No applications found for user: {}", userDetail.getEmail());
                generaicResponse.setMessage("Application not found");
                generaicResponse.setSuccess("false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
            }

            var application = applicantDetail.stream()
                    .filter(li -> Boolean.TRUE.equals(li.getStatus()))
                    .findAny()
                    .orElse(null);

            if (application != null) {

                log.info("Updating active application for user: {}", userDetail.getEmail());

                application.setAddress(userDetail.getAddress());
                application.setDateOfBirth(userDetail.getDateOfBirth());
                application.setEmployerName(userDetail.getEmployerName());
                application.setMonthlyIncome(userDetail.getMonthlyIncome());
                application.setUniversity(userDetail.getUniversity());
                application.setStep("1");

                applicationRepository.save(application);

                generaicResponse.setMessage("Records updated successfully.");
                generaicResponse.setSuccess("true");

                return ResponseEntity.ok(generaicResponse);

            } else {
                log.warn("No active application found for user: {}", userDetail.getEmail());
                generaicResponse.setMessage("Active application not found.");
                generaicResponse.setSuccess("false");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generaicResponse);
            }

        } catch (Exception e) {
            log.error("Error while creating/updating application for email: {}", userDetail.getEmail(), e);
            generaicResponse.setMessage("An error occurred");
            generaicResponse.setSuccess("false");
            return ResponseEntity.internalServerError().body(generaicResponse);
        }
    }
}