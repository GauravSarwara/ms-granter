package com.granter.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import com.granter.entity.PropertyDetails;
import com.granter.entity.SelfEmployedDetails;
import com.granter.entity.StudentDetails;
import com.granter.entity.User;
import com.granter.integrate.request.Activity;
import com.granter.integrate.request.DataPayload;
import com.granter.integrate.request.KonfirRequest;
import com.granter.integrate.request.Meta;
import com.granter.integrate.response.KonfirResponse;
import com.granter.repository.EmployedDetailsRepository;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.PropertyDetailsRepository;
import com.granter.repository.SelfEmployedDetailsRepository;
import com.granter.repository.StudentDetailsRepository;
import com.granter.repository.UserRepository;
import com.granter.request.ApplicantPropertyDetails;
import com.granter.request.ApplicationDetail;
import com.granter.service.ManageApplicantService;
import com.granter.utility.KonfirService;

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
    private final PropertyDetailsRepository propertyDetailsRepository;
    private final KonfirService konfirService; 
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
            userDetails.setMiddleName(user.getMiddleName());
            userDetails.setLastName(user.getLastName());
            userDetails.setMobileNo(user.getMobileNo());
            userDetails.setNationality(user.getNationality());
            userDetails.setProfessionType(user.getProfessionType());
            userDetails.setUserNumber(String.format("U%08d", user.getId()));
            List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());

            // ✅ Handle null or empty list
            if (applicantDetail == null || applicantDetail.isEmpty()) {
                log.warn("No applications found for user: {}", email);
                userDetails.setAppData(new ArrayList<>());
            } else {

                List<ApplicantDetail> listOfApplicantDetails = new ArrayList<>();
                List<ApplicantPropertyDetails> listOfPropertyDetails=new ArrayList<>();
                if (Boolean.TRUE.equals(active)) {
                    log.info("Fetching only active application for user: {}", email);

                    var application = applicantDetail.stream()
                            .filter(li -> Boolean.TRUE.equals(li.getStatus()))
                            .findAny()
                            .orElse(null);

                    if (application != null) {
                    	 userDetails.setStep(application.getStep());
                        listOfApplicantDetails.add(filUserDetails(application));
                        ApplicantPropertyDetails propertyDetail =new ApplicantPropertyDetails();
                        PropertyDetails dbpropertyDetails=  propertyDetailsRepository.findByGranterApplicationId(application.getId());
                      
                        propertyDetail.setAccommodationType(dbpropertyDetails.getAccommodationType());
                        propertyDetail.setLandlordName(dbpropertyDetails.getLandlordName());
                        propertyDetail.setMonthlyRent(dbpropertyDetails.getMonthlyRent());
                        propertyDetail.setTenancyEndDate(dbpropertyDetails.getTenancyEndDate());
                        propertyDetail.setTenancyStartDate(dbpropertyDetails.getTenancyStartDate());
                        propertyDetail.setPropertyAddress(dbpropertyDetails.getPropertyAddress());
                        propertyDetail.setEmail(email);
                        listOfPropertyDetails.add(propertyDetail);
                        userDetails.setPropertyDetail(listOfPropertyDetails);
                    } else {
                        log.warn("No active application found for user: {}", email);
                    }

                } else {
                    log.info("Fetching all applications for user: {}", email);

                    for (GranterApplication granterApplication : applicantDetail) {
                        if (granterApplication != null) {
                            listOfApplicantDetails.add(filUserDetails(granterApplication));
                            ApplicantPropertyDetails propertyDetail =new ApplicantPropertyDetails();
                            PropertyDetails dbpropertyDetails=  propertyDetailsRepository.findByGranterApplicationId(granterApplication.getId());
                          
                            propertyDetail.setAccommodationType(dbpropertyDetails.getAccommodationType());
                            propertyDetail.setLandlordName(dbpropertyDetails.getLandlordName());
                            propertyDetail.setMonthlyRent(dbpropertyDetails.getMonthlyRent());
                            propertyDetail.setTenancyEndDate(dbpropertyDetails.getTenancyEndDate());
                            propertyDetail.setTenancyStartDate(dbpropertyDetails.getTenancyStartDate());
                            propertyDetail.setPropertyAddress(dbpropertyDetails.getPropertyAddress());
                            propertyDetail.setEmail(email);
                            listOfPropertyDetails.add(propertyDetail);
                          
                        
                        }
                    }
                }
                userDetails.setPropertyDetail(listOfPropertyDetails);
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
		applicantData.setDateOfBirth(application.getBirthDate());
		applicantData.setAppNumber(String.format("AN%08d", application.getId()));
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
				application.setBirthDate(userDetail.getDateOfBirth());
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

	@Override
	public ResponseEntity<Object> createUserPropertyDetail(ApplicantPropertyDetails applicantPropertyDetails) {
		GeneraicResponse generaicResponse = new GeneraicResponse();

        try {
            log.info("createUserPropertyDetail application for email: {}", applicantPropertyDetails.getEmail());

            if (applicantPropertyDetails.getEmail() == null || applicantPropertyDetails.getEmail().isEmpty()) {
                log.warn("Email is missing in request");
                generaicResponse.setMessage("Email is required");
                generaicResponse.setSuccess("false");
                return ResponseEntity.badRequest().body(generaicResponse);
            }
            User user=userRepository.findByEmail(applicantPropertyDetails.getEmail());
            List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());

            if (applicantDetail == null || applicantDetail.isEmpty()) {
                log.warn("No applications found for user: {}", applicantPropertyDetails.getEmail());
                generaicResponse.setMessage("Application not found");
                generaicResponse.setSuccess("false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
            }

            var application = applicantDetail.stream()
                    .filter(li -> Boolean.TRUE.equals(li.getStatus()))
                    .findAny()
                    .orElse(null);
            GeneraicResponse validate=   validateApplicantPropertyDetails(applicantPropertyDetails);
            if(validate!=null) {
            	return ResponseEntity.badRequest().body(validate);
            }
            PropertyDetails propertyDetails=new PropertyDetails();
            propertyDetails.setAccommodationType(applicantPropertyDetails.getAccommodationType());
            propertyDetails.setLandlordName(applicantPropertyDetails.getLandlordName());
            propertyDetails.setPropertyAddress(applicantPropertyDetails.getPropertyAddress());
            propertyDetails.setTenancyStartDate(applicantPropertyDetails.getTenancyStartDate());
            propertyDetails.setTenancyEndDate(applicantPropertyDetails.getTenancyEndDate());
            propertyDetails.setMonthlyRent(applicantPropertyDetails.getMonthlyRent());
            propertyDetails.setGranterApplication(application);;;
            propertyDetailsRepository.save(propertyDetails);
            application.setStep("2");
            applicationRepository.save(application);
            generaicResponse.setMessage("User property detail created successfully.");
			generaicResponse.setSuccess("true");

			return ResponseEntity.ok(generaicResponse);
            
            
        }catch (Exception e) {
        	e.printStackTrace();
        	  log.error("Error while creating/updating application Property for email: {}", applicantPropertyDetails.getEmail(), e);
              generaicResponse.setMessage("An error occurred");
              generaicResponse.setSuccess("false");
              return ResponseEntity.internalServerError().body(generaicResponse);
		}
	}
	
	private GeneraicResponse validateApplicantPropertyDetails(ApplicantPropertyDetails req) {

	    if (req.getEmail() == null || req.getEmail().isEmpty()) {
	        log.warn("Email is missing in request");
	        return buildError("Email is required");
	    }

	    if (req.getAccommodationType() == null || req.getAccommodationType().isEmpty()) {
	        log.warn("Accommodation type is missing in request");
	        return buildError("Accommodation type is required");
	    }

	    if (req.getLandlordName() == null || req.getLandlordName().isEmpty()) {
	        log.warn("Landlord name is missing in request");
	        return buildError("Landlord name is required");
	    }

	    if (req.getPropertyAddress() == null || req.getPropertyAddress().isEmpty()) {
	        log.warn("Property address is missing in request");
	        return buildError("Property address is required");
	    }

	    if (req.getMonthlyRent() == null || req.getMonthlyRent().isEmpty()) {
	        log.warn("Monthly rent is missing in request");
	        return buildError("Monthly rent is required");
	    }

	    if (req.getTenancyStartDate() == null || req.getTenancyStartDate().isEmpty()) {
	        log.warn("Tenancy start date is missing in request");
	        return buildError("Tenancy start date is required");
	    }

	    if (req.getTenancyEndDate() == null || req.getTenancyEndDate().isEmpty()) {
	        log.warn("Tenancy end date is missing in request");
	        return buildError("Tenancy end date is required");
	    }

	    return null; // ✅ means validation passed
	}
	
	private GeneraicResponse buildError(String message) {
		GeneraicResponse generaicResponse=new GeneraicResponse();
	    generaicResponse.setMessage(message);
	    generaicResponse.setSuccess("false");
	    return generaicResponse;
	}

	@Override
	public ResponseEntity<Object> getVerifyKonfir(String email) {
		GeneraicResponse generaicResponse = new GeneraicResponse();
		try {
			var user = userRepository.findByEmail(email);
			// ✅ Null check for user
			if (user == null) {
				log.warn("User not found for email: {}", email);
				generaicResponse.setMessage("User not found");
				generaicResponse.setSuccess("false");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
			}

			KonfirRequest konfirRequest = new KonfirRequest();
			DataPayload dataPayload = new DataPayload();
			
			
			
			List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());
			var application = applicantDetail.stream().filter(li -> Boolean.TRUE.equals(li.getStatus())).findAny()
					.orElse(null);
			
			dataPayload.setFirstName(user.getFirstName());
			dataPayload.setLastName(user.getLastName());
			dataPayload.setPhoneNumber(user.getMobileNo());
			dataPayload.setDateOfBirth(convertDate(application.getBirthDate()));
			dataPayload.setNewEmployerName(user.getFirstName()+" "+user.getLastName());
			dataPayload.setEmail(email);
			
			
			List<Activity> listOfActivities = new ArrayList<>();
			Meta meta = new Meta();
			meta.setRedirectUrl("https://www.konfir.com");
			meta.setState("candidateid=123&test=verified");
			meta.setWebhookUrl("https://webhook-api.free.beeceptor.com");
			konfirRequest.setMeta(meta);
			if (application.getProfessions() != null) {
				for (ApplicationProfession ap : application.getProfessions()) {
					String professionName = ap.getProfession().getName();
					Activity activity = new Activity();
					switch (professionName) {

					case "STUDENT":

						StudentDetails student = application.getStudentDetails();
						if (student != null) {

							activity.setType("education");
							activity.setIsCurrent(true);
							activity.setStartDate(convertDate(student.getCourseStartDate()));
							activity.setEndDate(convertDate(student.getCourseEndDate()));
							activity.setVerifyViaKonfir(true);
							activity.setInstitution(student.getUniversity());

						}
						break;

					case "EMPLOYED":

						EmployedDetails employed = application.getEmployedDetails();
						if (employed != null) {

							activity.setVerifyViaKonfir(true);
							activity.setIsCurrent(true);
							activity.setStartDate(convertDate(employed.getDateOfJoining()));
							// activity.setEndDate(convertDate(employed.getCourseEndDate()));
							activity.setType("employment");
							activity.setCustomId("C00" + employed.getId());
							activity.setEmployerId(employed.getEmployeeId().toString());
							activity.setEmployerName(employed.getEmployerName());
							activity.setJobTitle(email);
							String monthlySalary = employed.getMonthlySalary();
							if (monthlySalary != null && !monthlySalary.trim().isEmpty()) {
								BigDecimal annualIncome = new BigDecimal(monthlySalary.trim())
										.multiply(BigDecimal.valueOf(12));
								activity.setDeclaredAnnualIncome(annualIncome);
							}
						}
						break;

					case "SELF_EMPLOYED":

						SelfEmployedDetails selfEmp = application.getSelfEmployedDetails();
						StudentDetails student1 = application.getStudentDetails();
						if (selfEmp != null) {
							activity.setType("self_employment");
							activity.setVerifyViaKonfir(true);
							activity.setIsCurrent(true);
							activity.setStartDate(convertDate(student1.getCourseStartDate()));
							
							activity.setCustomId("C00" + selfEmp.getId());
						}
						break;

					default:
						log.warn("Unknown profession type: {}", professionName);
					}

					listOfActivities.add(activity);
				}
				dataPayload.setActivities(listOfActivities);
				konfirRequest.setData(dataPayload);
				KonfirResponse konfirResponse=konfirService.createVerification(konfirRequest);
				generaicResponse.setData(konfirResponse);
				generaicResponse.setMessage("Request intiated");
				generaicResponse.setSuccess("true");

	            return ResponseEntity.ok(generaicResponse);
			}
	        } catch (Exception e) {
	            log.error("Error while fetching user details for email: {}", email, e);
	            generaicResponse.setMessage("An error occurred");
	            generaicResponse.setSuccess("false");
	            return ResponseEntity.internalServerError().body(generaicResponse);
	        }
		return  ResponseEntity.ok(generaicResponse);
	}
	private String convertDate(String courseStartDate) {
	    if (courseStartDate == null || courseStartDate.trim().isEmpty()) {
	        return null;
	    }

	    try { 
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        return LocalDate.parse(courseStartDate.trim(), inputFormatter)
	                .format(outputFormatter);

	    } catch (DateTimeParseException e) {
	        // Log the exception if you're using a logger
	        // log.error("Invalid date format: {}", courseStartDate, e);

	        return null;
	    } catch (Exception e) {
	        // Log unexpected exceptions
	        // log.error("Error while converting date: {}", courseStartDate, e);

	        return null;
	    }
	}
}