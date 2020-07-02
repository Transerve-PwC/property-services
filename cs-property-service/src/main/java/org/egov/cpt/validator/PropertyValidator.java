package org.egov.cpt.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
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
		validateOwner(request, errorMap);

		validateColony(request, errorMap);
		validateArea(request, errorMap);

		// TODO Commenting for temporary. Uncomment for payment validations
//		validatePayment(request, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	private void validatePayment(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			properties.getOwners().forEach(owners -> {
				owners.getOwnerDetails().getPayment().forEach(payment -> {
					if (!isNotNullValid(payment.getPaymentDate())) {
						errorMap.put("INVALID PAYMENT DATE", "Payment Date is not valid");
					}

					if (!isValid(payment.getPaymentMode(), 3, 15)) {
						errorMap.put("INVALID PAYMENT MODE", "Payment Mode is not valid");
					}
				});
			});
		});
	}

	private void validateOwner(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			properties.getOwners().forEach(owner -> {

				if (!isValid(owner.getOwnerDetails().getName(), 4, 40)) {
					errorMap.put("INVALID OWNER NAME", "Owner Name is not valid");
				}

				if (!isValid(owner.getOwnerDetails().getMonthlyRent(), 3, 20)) {
					errorMap.put("INVALID MONTHLY RENT", "Monthly Rent is not valid");
				}

				if (!isValid(owner.getOwnerDetails().getRevisionPeriod(), 1, 5)) {
					errorMap.put("INVALID REVISION PERIOD", "Revision Period is not valid");
				}

				if (!isValid(owner.getOwnerDetails().getRevisionPercentage(), 1, 5)) {
					errorMap.put("INVALID REVISION PERCENTAGE", "Revision Percentage is not valid");
				}

				if (!isNotNullValid(owner.getOwnerDetails().getPosessionStartdate())) {
					errorMap.put("INVALID POSESSION START DATE",
							"Posession Start date is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isNotNullValid(owner.getOwnerDetails().getAllotmentStartdate())) {
					errorMap.put("INVALID ALLOTMENT START DATE",
							"Allotment Start date is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isValid(owner.getAllotmenNumber(), 3, 20)) {
					errorMap.put("INVALID ALLOTMENT NUMBER",
							"Allotment Number is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isEmailValid(owner.getOwnerDetails().getEmail())) {
					errorMap.put("INVALID EMAIL", "Email is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isAadharNumberValid(owner.getOwnerDetails().getAadhaarNumber())) {
					errorMap.put("INVALID AADHARNUMBER",
							"Aadhar Number is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isMobileNumberValid(owner.getOwnerDetails().getPhone())) {
					errorMap.put("INVALID MOBILE NUMBER",
							"MobileNumber is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (!isValid(owner.getOwnerDetails().getFatherOrHusband(), 4, 40)) {
					errorMap.put("INVALID FATHER/HUSBAND NAME",
							"Father/Husband Name is not valid for user : " + owner.getOwnerDetails().getName());
				}

				if (owner.getOwnerDetails().getRelation() == null) {
					errorMap.put("INVALID FATHER/HUSBAND RELATION", "Father/Husband Relation is not valid ");
				}

			});
		});

	}

	private void validateArea(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> property = request.getProperties();
		property.forEach(properties -> {
			if (!isValid(properties.getPropertyDetails().getArea(), 4, 20)) {
				errorMap.put("INVALID AREA", "Area is not valid ");
			}

			if (properties.getPropertyDetails().getAddress().getPincode() == null
					|| properties.getPropertyDetails().getAddress().getPincode().length() != 6) {
				errorMap.put("INVALID PINCODE", "Pincode is not valid");
			}
		});

	}

	private void validateColony(PropertyRequest request, Map<String, String> errorMap) {

		String tenantId = request.getProperties().get(0).getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		request.getProperties().forEach(property -> {

			String filter = "$.*.code";
			Map<String, List<String>> colonies = getAttributeValues(tenantId.split("\\.")[0],
					PTConstants.MDMS_PT_EGF_PROPERTY_SERVICE, Arrays.asList("colonies"), filter,
					PTConstants.JSONPATH_COLONY, requestInfo);
			if (!colonies.get(PTConstants.MDMS_PT_COLONY).contains(property.getColony())
					|| (property.getColony().length() < 5 || property.getColony().length() > 45)) {
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
//			log.error("Error while fetching MDMS data", e);
			throw new CustomException("INVALID TENANT ID ", "No data found for this tenentID");
		}

	}

	private void validateTransitNumber(PropertyRequest request, Map<String, String> errorMap) {

		List<Property> prop = request.getProperties();
		prop.forEach(properties -> {
			if (properties.getTransitNumber().length() < 4 || properties.getTransitNumber().length() > 25) {
				errorMap.put("INVALID TRANSIT NUMBER", "Transit number is not valid ");
			}
		});

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

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

	/**
	 * Validates the masterData,CitizenInfo and the authorization of the assessee
	 * for update
	 * 
	 * @param request PropertyRequest for update
	 */
	public List<Property> validateUpdateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

		validateIds(request, errorMap);

		validateOwner(request, errorMap);

		validateColony(request, errorMap);
		validateArea(request, errorMap);
		
		// TODO Commenting for temporary. Uncomment for payment validations
//		validatePayment(request, errorMap);
		
		List<Property> prop = request.getProperties();
		prop.forEach(properties -> {
			if (properties.getTransitNumber().length() < 4 || properties.getTransitNumber().length() > 25) {
				errorMap.put("INVALID TRANSIT NUMBER", "Transit number is not valid ");
			}
		});

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		PropertyCriteria criteria = getPropertyCriteriaForSearch(request);
		List<Property> propertiesFromSearchResponse = repository.getProperties(criteria);
		boolean ifPropertyExists = PropertyExists(propertiesFromSearchResponse);
		if (!ifPropertyExists) {
			throw new CustomException("PROPERTY NOT FOUND", "The property to be updated does not exist");
		}

		propertiesFromSearchResponse.forEach(propertySearch -> {
			request.getProperties().forEach(property -> {

				compareIds(propertySearch.getId(), property.getId(), errorMap);
				compareIds(propertySearch.getPropertyDetails().getId(), property.getPropertyDetails().getId(),
						errorMap);
				compareIds(propertySearch.getPropertyDetails().getAddress().getId(),
						property.getPropertyDetails().getAddress().getId(), errorMap);
				compareIds(propertySearch.getId(), property.getPropertyDetails().getPropertyId(), errorMap);
				compareIds(propertySearch.getId(), property.getPropertyDetails().getAddress().getPropertyId(),
						errorMap);
				compareIds(propertySearch.getTransitNumber(),
						property.getPropertyDetails().getAddress().getTransitNumber(), errorMap);
				propertySearch.getOwners().forEach(searchOwner -> {
					property.getOwners().forEach(owner -> {
						compareIds(searchOwner.getId(), owner.getId(), errorMap);
						compareIds(propertySearch.getId(), owner.getPropertyId(), errorMap);
						compareIds(searchOwner.getOwnerDetails().getId(), owner.getOwnerDetails().getId(), errorMap);
						compareIds(propertySearch.getId(), owner.getOwnerDetails().getPropertyId(), errorMap);
						compareIds(searchOwner.getId(), owner.getOwnerDetails().getOwnerId(), errorMap);
					});
				});

			});
		});

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		return propertiesFromSearchResponse;
	}

	private void compareIds(String searchId, String updateId, Map<String, String> errorMap) {

		if (!(searchId.equals(updateId))) {
			errorMap.put("INVALID ID", "ID is not valid");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

	}

	private void validateIds(PropertyRequest request, Map<String, String> errorMap) {
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				if (!(property.getId() != null))
					errorMap.put("INVALID PROPERTY", "Property cannot be updated without propertyId");
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

		if (mobileNumber == null || mobileNumber == "")
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
		else if (email.length() < 2 || email.length() > 25)
			return false;
		else if (!email.matches(regex))
			return false;
		else
			return true;
	}

	private Boolean isValid(String value, int i, int j) {

		if (value == null || value.length() < i || value.length() > j) {
			return false;
		}
		return true;
	}

	private Boolean isNotNullValid(Long value) {
		if (value == null) {
			return false;
		}
		return true;
	}

}
