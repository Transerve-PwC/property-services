package org.egov.ps.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.model.Application;
import org.egov.ps.model.ApplicationCriteria;
import org.egov.ps.model.Property;
import org.egov.ps.model.Role;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.service.MDMSService;
import org.egov.ps.util.PSConstants;
import org.egov.ps.validator.application.MDMSValidator;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplicationValidatorService {

	/**
	 * A map whose keys are annotation names and whose values are the validator
	 * beans.
	 */
	Map<String, IApplicationValidator> validators;

	ApplicationContext context;

	MDMSService mdmsService;

	PropertyRepository propertyRepository;

	ObjectMapper objectMapper;

	@Autowired
	MDMSValidator mdmsValidator;

	@Autowired
	ApplicationValidatorService(ApplicationContext context, MDMSService mdmsService,
			PropertyRepository propertyRepository, ObjectMapper objectMapper) {
		this.context = context;
		this.mdmsService = mdmsService;
		this.propertyRepository = propertyRepository;
		this.objectMapper = objectMapper;
		//		this.mdmsValidator = mdmsValidator;
		Map<String, Object> beans = this.context.getBeansWithAnnotation(ApplicationValidator.class);

		/**
		 * Construct validators object by reading annotations from beans and discarding
		 * all beans that does not implement IApplicationValidator.
		 */
		this.validators = beans.entrySet().stream().filter(entry -> entry.getValue() instanceof IApplicationValidator)
				.collect(Collectors.toMap(e -> {
					ApplicationValidator annotation = this.context.findAnnotationOnBean(e.getKey(),
							ApplicationValidator.class);
					return annotation.value();
				}, e -> (IApplicationValidator) e.getValue()));
	}

	public void validateCreateRequest(ApplicationRequest request) {
		validateUserRole(request);
		List<Application> applications = request.getApplications();
		applications.stream().forEach(application -> {
			String propertyId = application.getProperty().getId();
			validatePropertyExists(application, propertyId);
			JsonNode applicationDetails = application.getApplicationDetails();
			try {
				String applicationDetailsString = this.objectMapper.writeValueAsString(applicationDetails);
				Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
				DocumentContext applicationObjectContext = JsonPath.using(conf).parse(applicationDetailsString);
				String moduleNameString = application.getBranchType() + "_" + application.getModuleType() + "_"
						+ application.getApplicationType();
				Map<String, List<String>> errorMap = this.performValidationsFromMDMS(moduleNameString,
						applicationObjectContext, request.getRequestInfo(), application.getTenantId());

				if (!errorMap.isEmpty()) {
					throw new CustomException("INVALID_FIELDS", "Please enter the valid fields " + errorMap.toString());
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		});
	}

	private void validateUserRole(ApplicationRequest request) {
		//fetch all user info roles...
		RequestInfo requestInfo = request.getRequestInfo();
		List<org.egov.common.contract.request.Role> roleList = null;
		if(null != requestInfo.getUserInfo()) {
			roleList = requestInfo.getUserInfo().getRoles();
			List<String> roleCode =  new ArrayList<String>(0);
					
			roleList.stream().forEach(r -> {
				roleCode.add(r.getName());
			});
		}

		//fetch all mdms data for branch type and check with user role is present...
		List<Application> applicationList = request.getApplications();
		for (Application application_ : applicationList) {
			
			List<Map<String, Object>> fieldConfigurations = mdmsService.getBranchRoles("branchtype", request.getRequestInfo(), "ch", application_.getBranchType());
			System.out.println(fieldConfigurations);
			
			ObjectMapper mapper = new ObjectMapper();
			List<Role> roleListMdMS = mapper.convertValue(fieldConfigurations, new TypeReference<List<Role>>() { });
			
			boolean error = true;
			for (Role r : roleListMdMS) {
				if(null != roleList && !roleList.isEmpty() && roleList.contains(r.getRole())) {
					error = false;
				}
			}
			
			if(error){
				throw new CustomException("INVALID ROLE",
						"ROLE is not valid for user : " + requestInfo.getUserInfo().getName());
			}
		}

	}

	private void validatePropertyExists(Application application, String propertyId) {
		Property property = propertyRepository.findPropertyById(application.getBranchType(), propertyId);
		if (property == null || !property.getState().contentEquals(PSConstants.PM_APPROVED)) {
			throw new CustomException("INVALID_PROPERTY", "Could not find property with the given id");
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<String>> performValidationsFromMDMS(String applicationType,
			DocumentContext applicationObject, RequestInfo RequestInfo, String tenantId) {
		List<Map<String, Object>> fieldConfigurations = this.mdmsService.getApplicationConfig(applicationType,
				RequestInfo, tenantId);
		Map<String, List<String>> errorsMap = new HashMap<String, List<String>>();
		for (int i = 0; i < fieldConfigurations.size(); i++) {
			Map<String, Object> fieldConfigMap = fieldConfigurations.get(i);

			/**
			 * Get field path from fieldConfig.
			 */
			String path = (String) fieldConfigMap.get("path");

			/**
			 * Get Value from applicationObject.
			 */
			Object value = applicationObject.read(path);
			List<Map<String, Object>> validationObjects = (List<Map<String, Object>>) fieldConfigMap.get("validations");

			/**
			 * Build IValidation object by reading from field configuration.
			 */
			List<IValidation> validations = validationObjects.stream()
					.map(validationObject -> ApplicationValidation.builder().type((String) validationObject.get("type"))
							.errorMessageFormat((String) validationObject.get("errorMessageFormat"))
							.params((Map<String, Object>) validationObject.get("params")).build())
					.collect(Collectors.toList());

			/**
			 * Construct the ApplicationField object.
			 */
			IApplicationField field = ApplicationField.builder().path(path)
					.required((boolean) fieldConfigMap.get("required")).rootObject(applicationObject).value(value)
					.validations(validations).build();

			/**
			 * Perform validations.
			 */
			List<String> errorMessages = this.performFieldValidations(applicationObject, field);
			if (errorMessages != null && !errorMessages.isEmpty()) {
				errorsMap.put(path, errorMessages);
			}
		}
		return errorsMap;
	}

	private static final String TYPE_REQUIRED = "required";

	private List<String> performFieldValidations(DocumentContext applicationObject, IApplicationField field) {

		Object value = applicationObject.read(field.getPath());

		/**
		 * Perform required validator validation first.
		 */
		IApplicationValidator requiredValidator = validators.get(TYPE_REQUIRED);
		List<String> requiredValidationErrors = requiredValidator
				.validate(ApplicationValidation.builder().type(TYPE_REQUIRED).build(), field, value, applicationObject);
		boolean isFieldEmpty = requiredValidationErrors != null && !requiredValidationErrors.isEmpty();

		if (field.isRequired() && isFieldEmpty) {
			log.debug("{} validator failed validating {} for path {}", TYPE_REQUIRED, value, field.getPath());
			return requiredValidationErrors;
		} else if (isFieldEmpty) {
			// field is not required and is empty. No validations to perform.
			return null;
		}

		/**
		 * Perform other validations and combine all the indivial error messages into a
		 * single list.
		 */
		return field.getValidations().stream().map(validation -> {
			IApplicationValidator validator = validators.get(validation.getType());
			if (validator == null) {
				log.error("No validator found for {} for path {}", validation.getType(), field.getPath());
				return null;
			}
			log.debug("{} validator validating {} for path {}", validation.getType(), value, field.getPath());
			return validator.validate(validation, field, value, applicationObject);
		}).filter(validationErrors -> validationErrors != null && !validationErrors.isEmpty())
				.reduce(new ArrayList<String>(), (a, b) -> {
					a.addAll(b);
					return a;
				});
	}

	public List<Application> getApplications(ApplicationRequest applicationRequest) {
		validateUserRole(applicationRequest);
		ApplicationCriteria criteria = getApplicationCriteria(applicationRequest);
		List<Application> applications = propertyRepository.getApplications(criteria);

		boolean ifApplicationExists = ApplicationExists(applications);
		if (!ifApplicationExists) {
			throw new CustomException("APPLICATION NOT FOUND", "The application to be updated does not exist");
		} else {
			return null;
		}
	}

	private boolean ApplicationExists(List<Application> applicationRequest) {
		return (!CollectionUtils.isEmpty(applicationRequest) && applicationRequest.size() == 1);
	}

	private ApplicationCriteria getApplicationCriteria(ApplicationRequest request) {
		ApplicationCriteria applicationCriteria = new ApplicationCriteria();
		if (!CollectionUtils.isEmpty(request.getApplications())) {
			request.getApplications().forEach(application -> {
				if (application.getId() != null)
					applicationCriteria.setApplicationId(application.getId());
			});
		}
		return applicationCriteria;
	}
}
