package org.egov.ps.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.ApplicationValidation;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.egov.ps.service.MDMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.minidev.json.JSONObject;

@Service
public class ApplicationValidatorService {

	Map<String, IApplicationValidator> validators = new HashMap<String, IApplicationValidator>();

	ApplicationContext context;

	MDMSService mdmsService;

	@Autowired
	ApplicationValidatorService(ApplicationContext context, MDMSService mdmsService) {
		this.context = context;
		this.mdmsService = mdmsService;
		Map<String, Object> beans = this.context.getBeansWithAnnotation(ApplicationValidator.class);

		beans.entrySet().stream().forEach(entry -> {
			ApplicationValidator annotation = this.context.findAnnotationOnBean(entry.getKey(),
					ApplicationValidator.class);
			if (entry.getValue() instanceof IApplicationValidator) {
				validators.put(annotation.value(), (IApplicationValidator) entry.getValue());
			}
		});
	}

	public void performValidationsFromMDMS(String applicationType, DocumentContext applicationObject,
			RequestInfo RequestInfo, String tenantId) {
		List<Map<String, Object>> fieldConfigurations = this.mdmsService.getApplicationConfig(applicationType,
				RequestInfo, tenantId);
		for (int i = 0; i < fieldConfigurations.size(); i++) {
			Map<String, Object> fieldConfigMap = fieldConfigurations.get(i);
			String path = (String) fieldConfigMap.get("path");
			Object value = applicationObject.read(path);
			List<Map<String, Object>> validationObjects = (List<Map<String, Object>>) fieldConfigMap.get("validations");
			System.out.println(validationObjects);
			List<IValidation> validations = validationObjects.stream()
					.map(validationObject -> ApplicationValidation.builder().type((String) validationObject.get("type"))
							.errorMessageFormat((String) validationObject.get("errorMessageFormat"))
							.params((Map<String, Object>) validationObject.get("params")).build())
					.collect(Collectors.toList());
			IApplicationField field = ApplicationField.builder().path(path)
					.required((boolean) fieldConfigMap.get("required")).rootObject(applicationObject).value(value)
					.validations(validations).build();
			this.performValidations(applicationObject, field);
		}
		System.out.println("Field configurations length" + fieldConfigurations);
	}

	private void performValidations(DocumentContext applicationObject, IApplicationField field) {
		for (int i = 0; i < field.getValidations().size(); i++) {
			IValidation validation = field.getValidations().get(i);
			IApplicationValidator validator = validators.get(validation.getType());
			if (validator == null) {
				System.out.println("No validator found for " + validation);
				return;
			}
			Object value = applicationObject.read(field.getPath());
			validator.isValid(validation, field, value, applicationObject);
		}
	}
}
