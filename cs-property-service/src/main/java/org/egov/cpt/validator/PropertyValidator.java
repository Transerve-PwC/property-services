package org.egov.cpt.validator;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {

	@Autowired
	private PropertyUtil propertyUtil;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private PropertyConfiguration propertyConfiguration;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private PropertyConfiguration configs;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	public void validateCreateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();
		
		validateTransitNumber(request, errorMap);
		
		validateEmail(request, errorMap);
		validAadharNumber(request, errorMap);
		validateMobileNumber(request, errorMap);
		validateColony(request, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	private void validateColony(PropertyRequest request, Map<String, String> errorMap) {

		String tenantId = request.getProperties().get(0).getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		request.getProperties().forEach(property -> {

			String filter = "$.*.code";
			Map<String, List<String>> colonies = getAttributeValues(tenantId.split("\\.")[0],
					PTConstants.MDMS_PT_EGF_PROPERTY_SERVICE, Arrays.asList("colonies"), filter,
					PTConstants.JSONPATH_COLONY, requestInfo);
			if (!colonies.get(PTConstants.MDMS_PT_COLONY).contains(property.getColony())) {
				errorMap.put("INVALID COLONY", "The Colony '" + property.getColony() + "' is not valid");
			}

			// If area need to be validated based on colonies(uncomment)

//			else {
//				String colonyFilter = "$.*.[?(@.code=='"+property.getColony()+"')].area.*.code";
//				Map<String, List<String>> area = getAttributeValues(tenantId.split("\\.")[0],
//						PTConstants.MDMS_PT_EGF_PROPERTY_SERVICE, Arrays.asList("colonies"), colonyFilter,
//						PTConstants.JSONPATH_COLONY, requestInfo);
//				if (!area.get(PTConstants.MDMS_PT_COLONY).contains(property.getPropertyDetails().getArea())) {
//					errorMap.put("INVALID AREA", "The Area '" + property.getPropertyDetails().getArea() + "' is not valid");
//				}
//			}

		});
	}

	private Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names,
			String filter, String jsonpath, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder(mdmsHost).append(mdmsEndpoint);

		MdmsCriteriaReq criteriaReq = propertyUtil.prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);

		try {
			Object result = serviceRequestRepository.fetchResult(uri, criteriaReq);
			return JsonPath.read(result, jsonpath);
		} catch (Exception e) {
			log.error("Error while fetvhing MDMS data", e);
			throw new CustomException("INVALID TENANT ID ", "No data found for this tenentID");
		}

	}

	private void validateEmail(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			properties.getOwners().forEach(owner -> {
				System.out.println(isEmailValid(owner.getEmail()));
				if (!isEmailValid(owner.getEmail())) {
					errorMap.put("INVALID EMAIL", "Email is not valid for user : " + owner.getName());
				}
			});
		});

	}

	private void validAadharNumber(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			properties.getOwners().forEach(owner -> {
				if (!isAadharNumberValid(owner.getAadhaarNumber())) {
					errorMap.put("INVALID AADHARNUMBER", "Aadhar Number is not valid for user : " + owner.getName());
				}
			});
		});

	}

	private void validateTransitNumber(PropertyRequest request, Map<String, String> errorMap) {

		PropertyCriteria criteria = new PropertyCriteria();
//      criteria.setTransitNumber(request.getProperties().get(0).getTransitNumber()); // TODO loop it array later

		List<Property> properties = repository.getProperties(criteria);

		properties.forEach(property -> {
			request.getProperties().forEach(transit -> {
				if (property.getTransitNumber().equalsIgnoreCase(transit.getTransitNumber())) {
					errorMap.put("INVALID TRANSIT NUMBER", "Transit number already exist");
				}
			});

		});

	}

	private void validateMobileNumber(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			properties.getOwners().forEach(owner -> {
				if (!isMobileNumberValid(owner.getPhone())) {
					errorMap.put("INVALID MOBILE NUMBER", "MobileNumber is not valid for user : " + owner.getName());
				}
			});
		});

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
		validateMobileNumber(request, errorMap);
		validateEmail(request, errorMap);
		validAadharNumber(request, errorMap);

		PropertyCriteria criteria = getPropertyCriteriaForSearch(request);
		List<Property> propertiesFromSearchResponse = repository.getProperties(criteria);
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

	/**
	 * Validates if the mobileNumber is 10 digit and starts with 5 or greater
	 * 
	 * @param mobileNumber The mobileNumber to be validated
	 * @return True if valid mobileNumber else false
	 */
	private Boolean isMobileNumberValid(String mobileNumber) {

		if (mobileNumber == null)
			return false;
		else if (mobileNumber.length() != 10)
			return false;
		else if (Character.getNumericValue(mobileNumber.charAt(0)) < 5)
			return false;
		else
			return true;
	}

	/**
	 * Validates if the aadharNumber is !12 digit
	 * 
	 * @param aadharNumber The aadharNumber to be validated
	 * @return True if valid aadharNumber else false
	 */
	private Boolean isAadharNumberValid(String aadharNumber) {

		if (aadharNumber == "" || aadharNumber == null)
			return true;
		else if (aadharNumber.length() != 12)
			return false;
		else
			return true;
	}

	/**
	 * Validates if the Email contains @ and .com or .xx
	 * 
	 * @param email The email to be validated
	 * @return True if valid email else false
	 */
	private Boolean isEmailValid(String email) {

		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

		if (email == "" || email == null)
			return true;
		else if (!email.matches(regex))
			return false;
		else
			return true;
	}

}
