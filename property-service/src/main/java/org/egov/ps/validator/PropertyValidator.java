package org.egov.ps.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.model.Role;
import org.egov.ps.model.EstateDocumentList;
import org.egov.ps.model.Owner;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
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

		validateUserRole(request, errorMap);
		validateOwner(request, errorMap);

	}

	private void validateUserRole(PropertyRequest request, Map<String, String> errorMap) {
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
		List<Property> propertyList = request.getProperties();
		for (Property property_ : propertyList) {
			
			List<Map<String, Object>> fieldConfigurations = mdmsservice.getBranchRoles("branchtype", request.getRequestInfo(), "ch", property_.getPropertyDetails().getBranchType());
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
				errorMap.put("INVALID ROLE",
						"ROLE is not valid for user : " + requestInfo.getUserInfo().getName());
			}
		}
		
	}

	private void validateOwner(PropertyRequest request, Map<String, String> errorMap) {


		Optional<Property> property_Optional = request.getProperties().stream().filter(p -> !CollectionUtils.isEmpty(p.getPropertyDetails().getOwners()))
				.findAny();
		if (property_Optional.isPresent()) {
			property_Optional.get().getPropertyDetails().getOwners().stream()
				.forEach(o -> {
					if (!isMobileNumberValid(o.getOwnerDetails().getMobileNumber())) {
						throw new CustomException(Collections.singletonMap("INVALID MOBILE NUMBER", String.format("MobileNumber is not valid for user :"+o.getOwnerDetails().getOwnerName(), o.getOwnerDetails().getOwnerName() )));
					}
					//Document Validation
					if(null != o.getOwnerDetails() && null != o.getOwnerDetails().getOwnerDocuments()) {
						validateDocumentsOnType(request.getRequestInfo(), property_Optional.get().getTenantId(), o, errorMap, "");
					}
					
				});
		}

		/* Old code ::
		property.forEach(properties -> {
			if (!CollectionUtils.isEmpty(properties.getPropertyDetails().getOwners())) {
				properties.getPropertyDetails().getOwners().forEach(owner -> {
					if (!isMobileNumberValid(owner.getOwnerDetails().getMobileNumber())) {
						errorMap.put("INVALID MOBILE NUMBER",
								"MobileNumber is not valid for user : " + owner.getOwnerDetails().getOwnerName());
					}
				});
			}
		});*/
	}
	
	public void validateDocumentsOnType(RequestInfo requestInfo, String tenantId, Owner owner, Map<String, String> errorMap, String code) {
		
		List<Map<String, Object>> fieldConfigurations = mdmsservice.getDocumentConfig("documents", requestInfo, tenantId);
		ObjectMapper mapper = new ObjectMapper();
		List<EstateDocumentList> documentTypeList = mapper.convertValue(fieldConfigurations, new TypeReference<List<EstateDocumentList>>() { });
		System.out.println(documentTypeList.size());
		
		
		owner.getOwnerDetails().getOwnerDocuments().stream().forEach(document -> {
			if(!documentTypeList.contains(EstateDocumentList.builder().code(document.getDocumentType()).build())) {
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

		validateUserRole(request, errorMap);

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
									//									errorMap.put("INVALID ModeOfTransfer", "value will only access types 'SALE', 'GIFT'");
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

			//			String modeOfTransferValue = application.getApplicationDetails().get("modeOfTransfer").asText();
			//			if (fields.get(PSConstants.MDMS_PS_FIELDS).contains("modeOfTransfer")) {
			//
			//				String validationFilter = "$.*.[?(@.name=='" + "modeOfTransfer" + "')].validations.*.type";
			//				Map<String, List<String>> validations = getAttributeValues(tenantId.split("\\.")[0], moduleName,
			//						Arrays.asList("fields"), validationFilter, jsonPath, requestInfo);
			//
			//				if (validations.get("fields").contains("enum")) {
			//					String valuesFilter = "$.*.[?(@.name=='" + "modeOfTransfer" + "')].validations.*.values.*";
			//					Map<String, List<String>> values = getAttributeValues(tenantId.split("\\.")[0], moduleName,
			//							Arrays.asList("fields"), valuesFilter, jsonPath, requestInfo);
			//
			//					if (!values.get("fields").contains(modeOfTransferValue)) {
			//						errorMap.put("INVALID ModeOfTransfer", "modeOfTransfer will only access types 'SALE', 'GIFT'");
			//					}
			//				}
			//			}

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
