package com.granter.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.granter.dto.ApplicantDetail;
import com.granter.dto.GeneraicResponse;
import com.granter.dto.UserDetails;
import com.granter.entity.ApplicationProfession;
import com.granter.entity.EmployedDetails;
import com.granter.entity.GranterApplication;
import com.granter.entity.SelfEmployedDetails;
import com.granter.entity.StudentDetails;
import com.granter.repository.EmployedDetailsRepository;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.SelfEmployedDetailsRepository;
import com.granter.repository.StudentDetailsRepository;
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
    private final EmployedDetailsRepository employedDetailsRepository;
    private final SelfEmployedDetailsRepository  selfEmployedDetailsRepository;
    private final StudentDetailsRepository studentDetailsRepository;

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

            List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());

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
		applicantData.setStatus(application.getStatus());
		applicantData.setStep(application.getStep());
		
		 // ✅ Profession-based mapping
			if (application.getProfessions() != null) {

				for (ApplicationProfession ap : application.getProfessions()) {

					String professionName = ap.getProfession().getName();

					switch (professionName) {

					case "STUDENT":

						StudentDetails student = application.getStudentDetails();
						if (student != null) {
							applicantData.setUniversity(student.getUniversity());
							applicantData.setCourse(student.getCourse());
							applicantData.setCourseStartDate(student.getCourseStartDate());
							applicantData.setCourseEndDate(student.getCourseEndDate());
						}
						break;

					case "EMPLOYED":

						EmployedDetails employed = application.getEmployedDetails();
						if (employed != null) {
							applicantData.setEmployerName(employed.getEmployerName());
							applicantData.setEmployerEmail(employed.getEmployerEmail());
							applicantData.setMonthlySalary(employed.getMonthlySalary());
							applicantData.setDateOfJoining(employed.getDateOfJoining());
							applicantData.setContractType(employed.getContractType());
						}
						break;

					case "SELF_EMPLOYED":

						SelfEmployedDetails selfEmp = application.getSelfEmployedDetails();
						if (selfEmp != null) {
							applicantData.setTradeName(selfEmp.getTradeName());
							applicantData.setTradeType(selfEmp.getTradeType());
							applicantData.setTurnover(selfEmp.getTurnover());
							applicantData.setProfit(selfEmp.getProfit());
							applicantData.setYearsOfExperience(selfEmp.getYearsOfExperience());
						}
						break;

					default:
						log.warn("Unknown profession type: {}", professionName);
					}
				}
			}

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

            List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());

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
				var listOfProfessions = application.getProfessions();
				for (ApplicationProfession applicationProfession : listOfProfessions) {

					String professionName = applicationProfession.getProfession().getName();

					switch (professionName) {

					case "STUDENT":

						StudentDetails student = StudentDetails.builder().application(application)
								.university(userDetail.getUniversity()).course(userDetail.getCourse())
								.courseStartDate(userDetail.getCourseStartDate())
								.courseEndDate(userDetail.getCourseEndDate()).build();

						studentDetailsRepository.save(student);
						break;

					case "EMPLOYED":

						EmployedDetails employed = EmployedDetails.builder().application(application)
								.employerName(userDetail.getEmployerName()).employerEmail(userDetail.getEmployerEmail())
								.monthlySalary(userDetail.getMonthlySalary())
								.dateOfJoining(userDetail.getDateOfJoining()).contractType(userDetail.getContractType())
								.build();

						employedDetailsRepository.save(employed);
						break;

					case "SELF_EMPLOYED":

						SelfEmployedDetails selfEmployed = SelfEmployedDetails.builder().application(application)
								.tradeName(userDetail.getTradeName()).tradeType(userDetail.getTradeType())
								.turnover(userDetail.getTurnover()).profit(userDetail.getProfit())
								.yearsOfExperience(userDetail.getYearsOfExperience()).build();

						selfEmployedDetailsRepository.save(selfEmployed);
						break;

					default:
						// nothing
					}
				}
				application.setStep("1");
				application.setUpdatedBy(user.getId() + "");
				application.setUpdatedAt(OffsetDateTime.now());
				application.setBirthDate(userDetail.getDateOfBirth());
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