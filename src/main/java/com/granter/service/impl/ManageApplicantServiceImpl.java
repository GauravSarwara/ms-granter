package com.granter.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.granter.dto.ApplicantDetail;
import com.granter.dto.GeneraicResponse;
import com.granter.dto.UserDetails;
import com.granter.entity.ApplicationProfession;
import com.granter.entity.EmployedDetails;
import com.granter.entity.GranterApplication;
import com.granter.entity.KonfirApiTransaction;
import com.granter.entity.PropertyDetails;
import com.granter.entity.SelfEmployedDetails;
import com.granter.entity.StudentDetails;
import com.granter.entity.User;
import com.granter.integrate.request.Activity;
import com.granter.integrate.request.DataPayload;
import com.granter.integrate.request.KonfirRequest;
import com.granter.integrate.request.Meta;
import com.granter.integrate.response.DataResponse;
import com.granter.integrate.response.KonfirResponse;
import com.granter.repository.EmployedDetailsRepository;
import com.granter.repository.GranterApplicationRepository;
import com.granter.repository.KonfirApiTransactionRepository;
import com.granter.repository.PropertyDetailsRepository;
import com.granter.repository.SelfEmployedDetailsRepository;
import com.granter.repository.StudentDetailsRepository;
import com.granter.repository.UserRepository;
import com.granter.request.ApplicantPropertyDetails;
import com.granter.request.ApplicationDetail;
import com.granter.request.VerificationCompletedWebhook;
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
	private final SelfEmployedDetailsRepository selfEmployedDetailsRepository;
	private final StudentDetailsRepository studentDetailsRepository;
	private final PropertyDetailsRepository propertyDetailsRepository;
	private final KonfirService konfirService;
	private final KonfirApiTransactionRepository konfirApiTransactionRepository;
	private ObjectMapper objectMapper=new ObjectMapper();
	

    @Value("${konfir.mete.redirect.url}")
    private String redirectUrl;

    @Value("${konfir.mete.webhook.url}")
    private String webhookUrl;

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
				List<ApplicantPropertyDetails> listOfPropertyDetails = new ArrayList<>();
				if (Boolean.TRUE.equals(active)) {
					log.info("Fetching only active application for user: {}", email);

					var application = applicantDetail.stream().filter(li -> Boolean.TRUE.equals(li.getStatus()))
							.findAny().orElse(null);

					if (application != null) {
						userDetails.setStep(application.getStep());
						listOfApplicantDetails.add(filUserDetails(application));
						ApplicantPropertyDetails propertyDetail = new ApplicantPropertyDetails();
						PropertyDetails dbpropertyDetails = propertyDetailsRepository
								.findByGranterApplicationId(application.getId());
						if (dbpropertyDetails != null) {

							propertyDetail.setAccommodationType(dbpropertyDetails.getAccommodationType());
							propertyDetail.setLandlordName(dbpropertyDetails.getLandlordName());
							propertyDetail.setMonthlyRent(dbpropertyDetails.getMonthlyRent());
							propertyDetail.setTenancyEndDate(dbpropertyDetails.getTenancyEndDate());
							propertyDetail.setTenancyStartDate(dbpropertyDetails.getTenancyStartDate());
							propertyDetail.setPropertyAddress(dbpropertyDetails.getPropertyAddress());
							propertyDetail.setEmail(email);
							listOfPropertyDetails.add(propertyDetail);
							userDetails.setPropertyDetail(listOfPropertyDetails);
						}
					} else {
						log.warn("No active application found for user: {}", email);
					}

				} else {
					log.info("Fetching all applications for user: {}", email);

					for (GranterApplication granterApplication : applicantDetail) {
						if (granterApplication != null) {
							listOfApplicantDetails.add(filUserDetails(granterApplication));
							ApplicantPropertyDetails propertyDetail = new ApplicantPropertyDetails();
							PropertyDetails dbpropertyDetails = propertyDetailsRepository
									.findByGranterApplicationId(granterApplication.getId());

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

			var application = applicantDetail.stream().filter(li -> Boolean.TRUE.equals(li.getStatus())).findAny()
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
			User user = userRepository.findByEmail(applicantPropertyDetails.getEmail());
			List<GranterApplication> applicantDetail = applicationRepository.findByUserId(user.getId());

			if (applicantDetail == null || applicantDetail.isEmpty()) {
				log.warn("No applications found for user: {}", applicantPropertyDetails.getEmail());
				generaicResponse.setMessage("Application not found");
				generaicResponse.setSuccess("false");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generaicResponse);
			}

			var application = applicantDetail.stream().filter(li -> Boolean.TRUE.equals(li.getStatus())).findAny()
					.orElse(null);
			GeneraicResponse validate = validateApplicantPropertyDetails(applicantPropertyDetails);
			if (validate != null) {
				return ResponseEntity.badRequest().body(validate);
			}
			PropertyDetails propertyDetails = new PropertyDetails();
			propertyDetails.setAccommodationType(applicantPropertyDetails.getAccommodationType());
			propertyDetails.setLandlordName(applicantPropertyDetails.getLandlordName());
			propertyDetails.setPropertyAddress(applicantPropertyDetails.getPropertyAddress());
			propertyDetails.setTenancyStartDate(applicantPropertyDetails.getTenancyStartDate());
			propertyDetails.setTenancyEndDate(applicantPropertyDetails.getTenancyEndDate());
			propertyDetails.setMonthlyRent(applicantPropertyDetails.getMonthlyRent());
			propertyDetails.setGranterApplication(application);
			;
			;
			propertyDetailsRepository.save(propertyDetails);
			application.setStep("2");
			applicationRepository.save(application);
			generaicResponse.setMessage("User property detail created successfully.");
			generaicResponse.setSuccess("true");

			return ResponseEntity.ok(generaicResponse);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while creating/updating application Property for email: {}",
					applicantPropertyDetails.getEmail(), e);
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
		GeneraicResponse generaicResponse = new GeneraicResponse();
		generaicResponse.setMessage(message);
		generaicResponse.setSuccess("false");
		return generaicResponse;
	}

	@Override
	public ResponseEntity<Object> getVerifyKonfir(String email) {

		log.info("Starting Konfir verification for email: {}", email);
		Map<String,Object> data=new HashMap<String,Object>();
		
		GeneraicResponse response = new GeneraicResponse();

		try {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ResponseEntity<Object> validation = validateEmail(email, response);
			if (validation != null) {
				return validation;
			}

			User user = getUser(email, response);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			Optional<KonfirApiTransaction> 	exitingRequest=konfirApiTransactionRepository.findByUserId(user.getId());
			if (exitingRequest.isPresent()) {
				KonfirApiTransaction existingTransaction = exitingRequest.get();
				if (existingTransaction.getStatus().equals("Completed")) {
					response.setMessage("Record Already verified.");
					response.setSuccess("true");
					data.put("konfirStatus", "Completed");
					data.put("email", email);
					data.put("meta", null);
					response.setData(data);
					return  ResponseEntity.ok(response);
				}
				KonfirResponse konfirResponse = objectMapper.readValue(existingTransaction.getResponseJson(),
						KonfirResponse.class);
				response.setSuccess("true");
				response.setMessage("Request already initiated");
				data.put("konfirStatus", "Pending");
				data.put("email", email);
				data.put("meta", konfirResponse.getMeta());
				response.setData(data);
				log.info("Konfir verification completed successfully.");
				return ResponseEntity.ok(response);
			}
			
			GranterApplication application = getActiveApplication(user.getId(), response);
			if (application == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			KonfirRequest request = buildKonfirRequest(user, application, email);
			String requestJson = objectMapper.writeValueAsString(request);
			KonfirApiTransaction transaction = createTransaction(user.getId(), requestJson);
			log.info("Calling Konfir API");
			KonfirResponse konfirResponse = konfirService.createVerification(request);
			if (konfirResponse == null) {
				log.error("Konfir service returned null response.");

				response.setSuccess("false");
				response.setMessage("Konfir service unavailable");
				data.put("konfirStatus", "Error");
				data.put("email", email);
				data.put("meta", null);
				response.setData(data);

				return ResponseEntity.internalServerError().body(response);
			}

			updateTransaction(transaction, konfirResponse);

			response.setSuccess("true");
			response.setMessage("Request initiated");
			data.put("konfirStatus", "Pending");
			data.put("email", email);
			data.put("meta", konfirResponse.getMeta());
			response.setData(data);

			log.info("Konfir verification completed successfully.");

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error while initiating Konfir verification", ex);

			response.setSuccess("false");
			response.setMessage("An unexpected error occurred");

			return ResponseEntity.internalServerError().body(response);
		}
	}

	// ============================================================
	// Validation
	// ============================================================

	private ResponseEntity<Object> validateEmail(String email, GeneraicResponse response) {

		if (email == null || email.isBlank()) {

			response.setSuccess("false");
			response.setMessage("Email is required");

			return ResponseEntity.badRequest().body(response);
		}

		return null;
	}

	// ============================================================
	// User
	// ============================================================

	private User getUser(String email, GeneraicResponse response) {

		User user = userRepository.findByEmail(email);

		if (user == null) {

			response.setSuccess("false");
			response.setMessage("User not found");
		}

		return user;
	}

	// ============================================================
	// Application
	// ============================================================

	private GranterApplication getActiveApplication(Long userId, GeneraicResponse response) {

		List<GranterApplication> applications = applicationRepository.findByUserId(userId);

		if (applications == null || applications.isEmpty()) {

			response.setSuccess("false");
			response.setMessage("Application not found");

			return null;
		}

		GranterApplication application = applications.stream().filter(a -> Boolean.TRUE.equals(a.getStatus()))
				.findFirst().orElse(null);

		if (application == null) {

			response.setSuccess("false");
			response.setMessage("Active application not found");
		}

		return application;
	}

	// ============================================================
	// Build Request
	// ============================================================

	private KonfirRequest buildKonfirRequest(User user, GranterApplication application, String email) {

		KonfirRequest request = new KonfirRequest();
		DataPayload payload = new DataPayload();
		payload.setFirstName(user.getFirstName());
		payload.setLastName(user.getLastName());
		payload.setPhoneNumber(user.getMobileNo());
		payload.setEmail(email);
		payload.setNewEmployerName(user.getFirstName() + " " + user.getLastName());
		if (application.getBirthDate() != null) {
			payload.setDateOfBirth(convertDate(application.getBirthDate()));
		}
		payload.setActivities(buildActivities(application, email));
		Meta meta = new Meta();
		meta.setRedirectUrl(redirectUrl);
		meta.setState("candidateid="+user.getId()+"&verified=true");
		meta.setWebhookUrl(webhookUrl);
		request.setMeta(meta);
		request.setData(payload);
		return request;
	}

	// ============================================================
	// Activities
	// ============================================================

	private List<Activity> buildActivities(GranterApplication application, String email) {

		List<Activity> activities = new ArrayList<>();

		if (application.getProfessions() == null) {
			return activities;
		}

		for (ApplicationProfession profession : application.getProfessions()) {

			if (profession == null || profession.getProfession() == null) {
				continue;
			}

			String type = profession.getProfession().getName();

			switch (type) {

			case "STUDENT":
				Activity student = buildStudentActivity(application);
				if (student != null)
					activities.add(student);
				break;

			case "EMPLOYED":
				Activity employed = buildEmploymentActivity(application, email);
				if (employed != null)
					activities.add(employed);
				break;

			case "SELF_EMPLOYED":
				Activity self = buildSelfEmploymentActivity(application);
				if (self != null)
					activities.add(self);
				break;

			default:
				log.warn("Unsupported profession {}", type);
			}
		}

		return activities;
	}

	// ============================================================
	// Student Activity
	// ============================================================

	private Activity buildStudentActivity(GranterApplication application) {

		StudentDetails student = application.getStudentDetails();
		if (student == null) {
			return null;
		}
		Activity activity = new Activity();
		activity.setType("education");
		activity.setIsCurrent(true);
		activity.setVerifyViaKonfir(true);
		activity.setInstitution(student.getUniversity());
		if (student.getCourseStartDate() != null) {
			activity.setStartDate(convertDate(student.getCourseStartDate()));
		}
		if (student.getCourseEndDate() != null) {
			activity.setEndDate(convertDate(student.getCourseEndDate()));
		}
		return activity;
	}

	// ============================================================
	// Employment Activity
	// ============================================================

	private Activity buildEmploymentActivity(GranterApplication application, String email) {

		EmployedDetails employed = application.getEmployedDetails();

		if (employed == null) {
			return null;
		}

		Activity activity = new Activity();

		activity.setType("employment");
		activity.setVerifyViaKonfir(true);
		activity.setIsCurrent(true);
		activity.setCustomId("C00" + employed.getId());
		activity.setEmployerName(employed.getEmployerName());
		activity.setJobTitle(email);

		if (employed.getDateOfJoining() != null) {
			activity.setStartDate(convertDate(employed.getDateOfJoining()));
		}

		if (employed.getEmployeeId() != null) {
			activity.setEmployerId(employed.getEmployeeId().toString());
		}

		if (employed.getMonthlySalary() != null && !employed.getMonthlySalary().isBlank()) {

			try {

				BigDecimal annualIncome = new BigDecimal(employed.getMonthlySalary()).multiply(BigDecimal.valueOf(12));

				activity.setDeclaredAnnualIncome(annualIncome);

			} catch (Exception ex) {

				log.warn("Invalid salary {}", employed.getMonthlySalary());
			}
		}

		return activity;
	}

	// ============================================================
	// Self Employment Activity
	// ============================================================

	private Activity buildSelfEmploymentActivity(GranterApplication application) {

		SelfEmployedDetails self = application.getSelfEmployedDetails();
		if (self == null) {
			return null;
		}
		Activity activity = new Activity();
		activity.setType("self_employment");
		activity.setVerifyViaKonfir(true);
		activity.setIsCurrent(true);
		activity.setCustomId("C00" + self.getId());
		StudentDetails student = application.getStudentDetails();
		if (student != null && student.getCourseStartDate() != null) {
			activity.setStartDate(convertDate(student.getCourseStartDate()));
		}
		return activity;
	}

	// ============================================================
	// Transaction
	// ============================================================

	private KonfirApiTransaction createTransaction(Long userId, String requestJson) {

		KonfirApiTransaction transaction = new KonfirApiTransaction();

		transaction.setUserId(userId);
		transaction.setRequestJson(requestJson);
		transaction.setStatus("In-Progress");

		return konfirApiTransactionRepository.save(transaction);
	}

	private void updateTransaction(KonfirApiTransaction transaction, KonfirResponse response) throws Exception {

		transaction.setResponseJson(objectMapper.writeValueAsString(response));

		DataResponse data = response.getData();

		if (data != null) {

			transaction.setCandidateId(data.getId());

			if (data.getActivities() != null) {

				data.getActivities().forEach(activity -> {

					switch (activity.getType().toLowerCase()) {

					case "education":
						transaction.setEducationActivityId(activity.getId());
						break;

					case "employment":
						transaction.setEmploymentActivityId(activity.getId());
						break;

					case "self_employment":
						transaction.setSelfEmploymentActivityId(activity.getId());
						break;
					}
				});
			}
		}
		transaction.setStatus("In-Progress");
		konfirApiTransactionRepository.save(transaction);
	}

	private String convertDate(String courseStartDate) {
		if (courseStartDate == null || courseStartDate.trim().isEmpty()) {
			return null;
		}

		try {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			return LocalDate.parse(courseStartDate.trim(), inputFormatter).format(outputFormatter);

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

	@Override
	public ResponseEntity<Object> konfirVerified(Long candidateid, Boolean verified) {
		GeneraicResponse generaicResponse = new GeneraicResponse();

		try {
			konfirApiTransactionRepository.findByUserId(candidateid).ifPresent(li -> {
				li.setStatus("Completed");
				konfirApiTransactionRepository.save(li);
			});
			
			List<GranterApplication> applicantDetail = applicationRepository.findByUserId(candidateid);

			var application = applicantDetail.stream().filter(li -> Boolean.TRUE.equals(li.getStatus()))
					.findAny().orElse(null);
			if(application!=null) {
				application.setStep("3");
				applicationRepository.save(application);;
			}
			generaicResponse.setMessage("Record updated successfully.");
			generaicResponse.setSuccess("true");
			return ResponseEntity.ok(generaicResponse);
		} catch (Exception e) {
			e.printStackTrace();
			generaicResponse.setMessage("An error occurred.");
			generaicResponse.setSuccess("false");
			return ResponseEntity.internalServerError().body(generaicResponse);
		}

	}

	@Override
	public ResponseEntity<Object> konfirVerifiedByWebHook(VerificationCompletedWebhook request) {
		try {
			
			if (request != null && request.getEvent() != null && request.getEvent().equalsIgnoreCase("verificationCompleted")) {								
					konfirApiTransactionRepository.findByCandidateId(request.getData().getId()).ifPresent(li -> {
						li.setStatus("Completed");
						konfirApiTransactionRepository.save(li);						
						List<GranterApplication> applicantDetail = applicationRepository.findByUserId(li.getUserId());
						var application = applicantDetail.stream().filter(app -> Boolean.TRUE.equals(app.getStatus()))
								.findAny().orElse(null);
						if(application!=null) {
							application.setStep("3");
							applicationRepository.save(application);;
						}
					});			
			}
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Failed");
		}
	}
}