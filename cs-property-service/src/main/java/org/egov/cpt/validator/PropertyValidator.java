package org.egov.cpt.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {

	@Autowired
	private PropertyRepository propertyRepository;

	public void validateCreateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

//		validateMasterData(request, errorMap);
//		validateMobileNumber(request, errorMap);
//		validateFields(request, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validates the masterData,CitizenInfo and the authorization of the assessee
	 * for update
	 * 
	 * @param request PropertyRequest for update
	 */
	public List<Property> validateUpdateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

		validateIds(request, errorMap);
//		validateMobileNumber(request, errorMap);

		PropertyCriteria criteria = getPropertyCriteriaForSearch(request);
		List<Property> propertiesFromSearchResponse = propertyRepository.getProperties(criteria);
		boolean ifPropertyExists = PropertyExists(propertiesFromSearchResponse);
		if (!ifPropertyExists) {
			throw new CustomException("PROPERTY NOT FOUND", "The property to be updated does not exist");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		return propertiesFromSearchResponse;
	}

	private void validateIds(PropertyRequest request, Map<String, String> errorMap) {
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				if (!(property.getId() != null))
					errorMap.put("INVALID PROPERTY",
							"Property cannot be updated without propertyId or acknowledgement number");
				if (!errorMap.isEmpty())
					throw new CustomException(errorMap);
			});
		}
	}

	public PropertyCriteria getPropertyCriteriaForSearch(PropertyRequest request) {
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				if (property.getTransitNumber() != null)
					propertyCriteria.setTransitNumber(property.getTransitNumber());
				if (property.getColony() != null)
					propertyCriteria.setColony(property.getColony());
			});
		}
		return propertyCriteria;
	}

	public boolean PropertyExists(List<Property> responseProperties) {
		return (!CollectionUtils.isEmpty(responseProperties) && responseProperties.size() == 1);
	}

}
