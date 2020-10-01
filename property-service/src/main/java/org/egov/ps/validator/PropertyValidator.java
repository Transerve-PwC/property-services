package org.egov.ps.validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.model.EstateDocumentList;
import org.egov.ps.model.Owner;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.model.Role;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.repository.ServiceRequestRepository;
import org.egov.ps.service.MDMSService;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyValidator {

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private Util util;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	@Autowired
	private MDMSService mdmsservice;

	public void validateCreateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

		validateProperty(request, errorMap);
//		validateUserRole(request, errorMap);
		validateOwner(request, errorMap);

	}

	private void validateProperty(PropertyRequest request, Map<String, String> errorMap) {
		PropertyCriteria criteria = getPropertyCriteriaForSearch(request);
		List<Property> properties = repository.getProperties(criteria);
		Property requestProperty = request.getProperties().get(0);
		if (!CollectionUtils.isEmpty(properties)) {
			properties.forEach(property -> {
				if (property.getFileNumber().equalsIgnoreCase(requestProperty.getFileNumber().trim())) {
					errorMap.put("FILE_NUMBER_ALREADY_EXIST", "The given File Number already exists");
				}
			});
		}

		if (requestProperty.getFileNumber() == null || requestProperty.getFileNumber().trim().isEmpty()) {
			errorMap.put("INVALID_FILE_NUMBER", "File Number can not be empty");
		}
		if (requestProperty.getCategory() == null || requestProperty.getCategory().trim().isEmpty()) {
			errorMap.put("INVALID_CATEGORY", "Category can not be empty");
		}
		if (requestProperty.getSubCategory() == null || requestProperty.getSubCategory().trim().isEmpty()) {
			errorMap.put("INVALID_SUB_CATEGORY", "Sub category can not be empty");
		}
		if (requestProperty.getSiteNumber() == null || requestProperty.getSiteNumber().trim().isEmpty()) {
			errorMap.put("INVALID_SITE_NUMBER", "Site number can not be empty");
		}
		if (requestProperty.getPropertyDetails().getPropertyType() == null || requestProperty.getPropertyDetails().getPropertyType().trim().isEmpty()) {
			errorMap.put("INVALID_PROPERTY_TYPE", "Property type can not be empty");
		}
		if (requestProperty.getPropertyDetails().getTypeOfAllocation() == null || requestProperty.getPropertyDetails().getTypeOfAllocation().trim().isEmpty()) {
			errorMap.put("INVALID_TYPE_OF_ALLOCATION", "Type of allocation can not be empty");
		}
		if (requestProperty.getPropertyDetails().getEmdAmount().signum() < 1) {
			errorMap.put("INVALID_EMD_AMOUNT", "EMD amount can not be less than or equals to zero");
		}
		if (requestProperty.getPropertyDetails().getEmdDate() == null) {
			errorMap.put("INVALID_EMD_DATE", "EMD date can not be empty");
		}
		if (requestProperty.getPropertyDetails().getModeOfAuction() == null || requestProperty.getPropertyDetails().getModeOfAuction().trim().isEmpty()) {
			errorMap.put("INVALID_MODE_OF_AUCTION", "Mode of auction can not be empty");
		}
		if (requestProperty.getPropertyDetails().getSchemeName() == null || requestProperty.getPropertyDetails().getSchemeName().trim().isEmpty()) {
			errorMap.put("INVALID_SCHEME_NAME", "Scheme name can not be empty");
		}
		if (requestProperty.getPropertyDetails().getDateOfAuction() == null) {
			errorMap.put("INVALID_DATE_OF_AUCTION", "Date of auction can not be empty");
		}
		if (requestProperty.getPropertyDetails().getAreaSqft() < 1) {
			errorMap.put("INVALID_AREA_SQ_FT", "Area per sq.ft can not be empty");
		}
		if (requestProperty.getPropertyDetails().getRatePerSqft().signum() < 1) {
			errorMap.put("INVALID_RATE_PER_SQFT", "Rate per sq.ft can not be less than or equals to zero");
		}
		
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

	public void validateUserRole(PropertyRequest request, Map<String, String> errorMap) {
		// fetch all user info roles...
		RequestInfo requestInfo = request.getRequestInfo();
		if (CollectionUtils.isEmpty(requestInfo.getUserInfo().getRoles())) {
			throw new CustomException("INVALID_USER_ROLE", "User roles not found in request");
		}

		// roleCodes = {"EMPLOYEE", "ES_EB_APPROVER", "ES_EB_DSO"}
		List<String> roleCodes = requestInfo.getUserInfo().getRoles().stream()
				.map(org.egov.common.contract.request.Role::getName).collect(Collectors.toList());

		// fetch all mdms data for branch type
		List<Map<String, Object>> fieldConfigurations = mdmsservice.getBranchRoles("branchType",
				request.getRequestInfo(), request.getProperties().get(0).getTenantId());
		List<Role> roleListMdMS = new ObjectMapper().convertValue(fieldConfigurations, new TypeReference<List<Role>>() {
		});

		// check with user role is present...
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property_ -> {
				String branchType = property_.getPropertyDetails().getBranchType();

				Optional<Role> mdmsRoleOptional = roleListMdMS.stream()
						.filter(mdmsRole -> mdmsRole.getCode().equalsIgnoreCase(branchType))
						.filter(mdmsRole -> roleCodes.contains(mdmsRole.getRole())).findAny();
				if (!mdmsRoleOptional.isPresent()) {
					String joinedRoleCodes = roleCodes.stream().reduce("", String::concat);
					errorMap.put("INVALID_ROLE",
							String.format("Property %s is not allowed to be created by user with roles %s",
									property_.getFileNumber(), joinedRoleCodes));
				}
			});
		}
	}

	private void validateOwner(PropertyRequest request, Map<String, String> errorMap) {

		Optional<Property> property_Optional = request.getProperties().stream()
				.filter(p -> !CollectionUtils.isEmpty(p.getPropertyDetails().getOwners())).findAny();
		if (property_Optional.isPresent()) {
			property_Optional.get().getPropertyDetails().getOwners().stream().forEach(o -> {
				if (!isMobileNumberValid(o.getOwnerDetails().getMobileNumber())) {
					throw new CustomException(Collections.singletonMap("INVALID MOBILE NUMBER",
							String.format("MobileNumber is not valid for user :" + o.getOwnerDetails().getOwnerName(),
									o.getOwnerDetails().getOwnerName())));
				}
				// Document Validation
				if (null != o.getOwnerDetails() && null != o.getOwnerDetails().getOwnerDocuments()) {
					validateDocumentsOnType(request.getRequestInfo(), property_Optional.get().getTenantId(), o,
							errorMap, "");
				}

			});
		}

		/*
		 * Old code :: property.forEach(properties -> { if
		 * (!CollectionUtils.isEmpty(properties.getPropertyDetails().getOwners())) {
		 * properties.getPropertyDetails().getOwners().forEach(owner -> { if
		 * (!isMobileNumberValid(owner.getOwnerDetails().getMobileNumber())) {
		 * errorMap.put("INVALID MOBILE NUMBER", "MobileNumber is not valid for user : "
		 * + owner.getOwnerDetails().getOwnerName()); } }); } });
		 */
	}

	public void validateDocumentsOnType(RequestInfo requestInfo, String tenantId, Owner owner,
			Map<String, String> errorMap, String code) {

		List<Map<String, Object>> fieldConfigurations = mdmsservice.getDocumentConfig("documents", requestInfo,
				tenantId);
		ObjectMapper mapper = new ObjectMapper();
		List<EstateDocumentList> documentTypeList = mapper.convertValue(fieldConfigurations,
				new TypeReference<List<EstateDocumentList>>() {
				});
		System.out.println(documentTypeList.size());

		owner.getOwnerDetails().getOwnerDocuments().stream().forEach(document -> {
			if (!documentTypeList.contains(EstateDocumentList.builder().code(document.getDocumentType()).build())) {
				errorMap.put("INVALID DOCUMENT",
						"Document is not valid for user : " + owner.getOwnerDetails().getOwnerName());
			}
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

	public List<Property> validateUpdateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

//		validateUserRole(request, errorMap);

		PropertyCriteria criteria = getPropertyCriteriaForSearch(request);
		List<Property> propertiesFromSearchResponse = repository.getProperties(criteria);
		boolean ifPropertyExists = PropertyExists(propertiesFromSearchResponse);
		if (!ifPropertyExists) {
			throw new CustomException("PROPERTY NOT FOUND", "The property to be updated does not exist");
		}

		return null; // TODO: add next lines
	}

	private boolean PropertyExists(List<Property> propertiesFromSearchResponse) {

		return (!CollectionUtils.isEmpty(propertiesFromSearchResponse) && propertiesFromSearchResponse.size() == 1);
	}

	private PropertyCriteria getPropertyCriteriaForSearch(PropertyRequest request) {

		PropertyCriteria propertyCriteria = new PropertyCriteria();
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				if (property.getId() != null)
					propertyCriteria.setPropertyId(property.getId());

			});
		}
		return propertyCriteria;
	}

	public void validateRequest(ApplicationRequest request) {
		Map<String, String> errorMap = new HashMap<>();
		String tenantId = request.getApplications().get(0).getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		request.getApplications().forEach(application -> {

			String filter = "$.*.name";
			String moduleName = application.getBranchType() + "_" + application.getModuleType() + "_"
					+ application.getApplicationType();
			String jsonPath = "$.MdmsRes." + moduleName;

			Map<String, List<String>> fields = getAttributeValues(tenantId.split("\\.")[0], moduleName,
					Arrays.asList("fields"), filter, jsonPath, requestInfo);

			for (Map.Entry<String, List<String>> field : fields.entrySet()) {
				List<String> values = field.getValue();
				for (String value : values) {

					if (application.getApplicationDetails().has(value) || application.getProperty().getId() != null) {

						if (fields.get(PSConstants.MDMS_PS_FIELDS).contains(value)) {

							String validationFilter = "$.*.[?(@.name=='" + value + "')].validations.*.type";
							Map<String, List<String>> validations = getAttributeValues(tenantId.split("\\.")[0],
									moduleName, Arrays.asList("fields"), validationFilter, jsonPath, requestInfo);

							System.out.println(validations.get("fields"));
							if (validations.get("fields").contains("enum")) {
								String valuesFilter = "$.*.[?(@.name=='" + value + "')].validations.*.values.*";
								Map<String, List<String>> values1 = getAttributeValues(tenantId.split("\\.")[0],
										moduleName, Arrays.asList("fields"), valuesFilter, jsonPath, requestInfo);

								if (!values1.get("fields")
										.contains(application.getApplicationDetails().get(value).asText())) {
									// errorMap.put("INVALID ModeOfTransfer", "value will only access types 'SALE',
									// 'GIFT'");
									System.out.println("error");
									String errorFilter = "$.*.[?(@.name=='" + value + "')].validations.*.errorMessage";
									Map<String, List<String>> error = getAttributeValues(tenantId.split("\\.")[0],
											moduleName, Arrays.asList("fields"), errorFilter, jsonPath, requestInfo);

									throw new CustomException("ERROR FIELD", error.toString());
								}
							}
						}
					}
				}
			}

			// String modeOfTransferValue =
			// application.getApplicationDetails().get("modeOfTransfer").asText();
			// if (fields.get(PSConstants.MDMS_PS_FIELDS).contains("modeOfTransfer")) {
			//
			// String validationFilter = "$.*.[?(@.name=='" + "modeOfTransfer" +
			// "')].validations.*.type";
			// Map<String, List<String>> validations =
			// getAttributeValues(tenantId.split("\\.")[0], moduleName,
			// Arrays.asList("fields"), validationFilter, jsonPath, requestInfo);
			//
			// if (validations.get("fields").contains("enum")) {
			// String valuesFilter = "$.*.[?(@.name=='" + "modeOfTransfer" +
			// "')].validations.*.values.*";
			// Map<String, List<String>> values =
			// getAttributeValues(tenantId.split("\\.")[0], moduleName,
			// Arrays.asList("fields"), valuesFilter, jsonPath, requestInfo);
			//
			// if (!values.get("fields").contains(modeOfTransferValue)) {
			// errorMap.put("INVALID ModeOfTransfer", "modeOfTransfer will only access types
			// 'SALE', 'GIFT'");
			// }
			// }
			// }

		});
	}

	private Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names,
			String filter, String jsonpath, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder(mdmsHost).append(mdmsEndpoint);

		MdmsCriteriaReq criteriaReq = util.prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);

		try {
			Object result = serviceRequestRepository.fetchResult(uri, criteriaReq);
			return JsonPath.read(result, jsonpath);
		} catch (Exception e) {
			throw new CustomException("INVALID TENANT ID ", e.toString());
		}
	}

}
