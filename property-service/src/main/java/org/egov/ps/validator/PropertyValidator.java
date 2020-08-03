package org.egov.ps.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.model.Property;
import org.egov.ps.web.contracts.PropertyRequest;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {
	
	public void validateCreateRequest(PropertyRequest request) {
		
		Map<String, String> errorMap = new HashMap<>();
		
		validateOwner(request, errorMap);
		
		
	}

	private void validateOwner(PropertyRequest request, Map<String, String> errorMap) {
		 
		List<Property> property = request.getProperties();
		
		property.forEach(properties -> {
			properties.getPropertyDetails().getOwners().forEach(owner -> {
				
				if (!isMobileNumberValid(owner.getOwnerDetails().getMobileNumber())) {
					errorMap.put("INVALID MOBILE NUMBER",
							"MobileNumber is not valid for user : " + owner.getOwnerDetails().getOwnerName());
				}
			});
		});
	}

	private boolean isMobileNumberValid(String mobileNumber) {
		if (mobileNumber == null || mobileNumber == "")
			return false;
		else if (mobileNumber.length() != 10)
			return false;
		else if (Character.getNumericValue(mobileNumber.charAt(0)) < 5)
			return false;
		else
			return true;
	}

}
