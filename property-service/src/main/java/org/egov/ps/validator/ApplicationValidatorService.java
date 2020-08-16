package org.egov.ps.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jayway.jsonpath.DocumentContext;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.ApplicationValidation;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.egov.ps.service.MDMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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

	@Autowired
	ApplicationValidatorService(ApplicationContext context, MDMSService mdmsService) {
		this.context = context;
		this.mdmsService = mdmsService;
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
}
