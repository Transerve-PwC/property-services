package org.egov.cpt.validator;

import java.util.HashMap;
import java.util.Map;

import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {

	public void validateCreateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

//		validateMasterData(request, errorMap);
//		validateMobileNumber(request, errorMap);
//		validateFields(request, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}
}
