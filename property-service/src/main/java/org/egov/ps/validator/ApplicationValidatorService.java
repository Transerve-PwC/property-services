package org.egov.ps.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.IApplicationField;
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
			System.out.println(annotation.value());
			if (entry.getValue() instanceof IApplicationValidator) {
				validators.put(annotation.value(), (IApplicationValidator) entry.getValue());
			}
		});
	}

	public void performValidationsFromMDMS(String applicationType, Map<String, Object> applicationObject,
			RequestInfo RequestInfo, String tenantId) {
		List<Map<String, Object>> fieldConfigurations = this.mdmsService.getApplicationConfig(applicationType,
				RequestInfo, tenantId);
		for (int i = 0; i < fieldConfigurations.size(); i++) {
			Map<String, Object> fieldConfigMap = fieldConfigurations.get(i);
			String path = (String) fieldConfigMap.get("path");
			Object value = applicationObject.get(path);
			IApplicationField field = ApplicationField.builder().path(path)
					.required((boolean) fieldConfigMap.get("required")).rootObject(applicationObject).value(value)
					.build();
			System.out.println(field);
		}
		System.out.println("Field configurations length" + fieldConfigurations);
	}

	public void performValidations(JSONObject applicationObject, List<IApplicationField> fields) {

	}
}
