package org.egov.cpt.validator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
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

		// validateMasterData(request, errorMap);
		// validateMobileNumber(request, errorMap);
		// validateFields(request, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * Validates the masterData,CitizenInfo and the authorization of the
	 * assessee for update
	 * 
	 * @param request
	 *            PropertyRequest for update
	 */
	public List<Property> validateUpdateRequest(PropertyRequest request) {

		Map<String, String> errorMap = new HashMap<>();

		validateIds(request, errorMap);
		// validateMobileNumber(request, errorMap);

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

	public void validateDuplicateCopySearch(RequestInfo requestInfo, PropertyCriteria criteria) {
		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN") && criteria == null)
			throw new CustomException("INVALID SEARCH", "Search without any paramters is not allowed");
		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN") && criteria.getTransitNumber() == null)
			throw new CustomException("INVALID SEARCH", "Transit number is mandatory in search");
	}

	public List<DuplicateCopy> validateDuplicateCopyUpdateRequest(DuplicateCopyRequest duplicateCopyRequest) {
		Map<String, String> errorMap = new HashMap<>();

		validateDocument(duplicateCopyRequest);
		validateIds(duplicateCopyRequest);

		// validateIds(duplicateCopyRequest, errorMap);
		String transitNo = duplicateCopyRequest.getProperties().get(0).getTransitNumber();
		PropertyCriteria criteria = PropertyCriteria.builder().transitNumber(transitNo)
				.propertyId(duplicateCopyRequest.getProperties().get(0).getId()).build();
		List<DuplicateCopy> searchedProperties = propertyRepository.getDuplicateCopyProperties(criteria);
		if (searchedProperties.size() < 1) {
			errorMap.put("PROPERTY NOT FOUND", "The property to be updated does not exist");
		}
		if (searchedProperties.size() > 1) {
			errorMap.put("INVALID PROPERTY", "Multiple property found");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		return searchedProperties;
	}

	private void validateIds(DuplicateCopyRequest request) {
		Map<String,String> errorMap = new HashMap<>();
        request.getProperties().forEach(property -> {

            if((!property.getState().equalsIgnoreCase(PTConstants.STATUS_INITIATED)))
            {
                    if (property.getId() == null)
                        errorMap.put("INVALID UPDATE", "Id of property cannot be null");
                    if(property.getPropertyDetails().getId()==null)
                        errorMap.put("INVALID UPDATE", "Id of propertyDetail cannot be null");
                    /*if(property.getPropertyDetails().getAddress().getId()==null)
                        errorMap.put("INVALID UPDATE", "Id of address cannot be null");*/
                    if(property.getApplicant().get(0).getId()==null)
                    	errorMap.put("INVALID UPDATE", "Id of Applicant cannot be null");
            }
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
	}

	public void validateDuplicateCopyCreateRequest(DuplicateCopyRequest duplicateCopyRequest) {
		validateDocument(duplicateCopyRequest);

	}

	private void validateDocument(DuplicateCopyRequest duplicateCopyRequest) {
		Map<String, String> errorMap = new HashMap<>();

		duplicateCopyRequest.getProperties().forEach(property -> {

			/*
			 * if
			 * (PTConstants.ACTION_INITIATE.equalsIgnoreCase(property.getAction(
			 * ))) { if (property.getPropertyDetails().getApplicationDocuments()
			 * != null) errorMap.put("INVALID ACTION",
			 * "Action should be APPLY when application document are provided");
			 * }
			 */
			if (PTConstants.ACTION_SUBMIT.equalsIgnoreCase(property.getAction())) {
				if (property.getPropertyDetails().getApplicationDocuments() == null)
					errorMap.put("INVALID ACTION",
							"Action cannot be changed to SUBMIT. Application document are not provided");
			}
			/*if (!PTConstants.ACTION_SUBMIT.equalsIgnoreCase(property.getAction())
					&& !PTConstants.ACTION_INITIATE.equalsIgnoreCase(property.getAction())) {
				errorMap.put("INVALID ACTION", "Action can only be SUBMIT or INITIATE during create");
			}*/

		});

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	public void validateDuplicateCreate(DuplicateCopyRequest duplicateCopyRequest) {
		// valideDates(request, mdmsData);
		// propertyValidator.validateProperty(request);
		// validatePTSpecificNotNullFields(duplicateCopyRequest);
		validateDuplicateDocuments(duplicateCopyRequest);

	}

	private void validateTLSpecificNotNullFields(DuplicateCopyRequest duplicateCopyRequest) {

	}

	private void validateDuplicateDocuments(DuplicateCopyRequest request) {
		List<String> documentFileStoreIds = new LinkedList();
		request.getProperties().forEach(property -> {
			if(property.getPropertyDetails()!=null){
				if (property.getPropertyDetails().getApplicationDocuments() != null) {
					property.getPropertyDetails().getApplicationDocuments().forEach(document -> {
						if (documentFileStoreIds.contains(document.getFileStoreId()))
							throw new CustomException("DUPLICATE_DOCUMENT ERROR",
									"Same document cannot be used multiple times");
						else
							documentFileStoreIds.add(document.getFileStoreId());
					});
				}
			}
		});
	}

	public void validateDuplicateUpdate(DuplicateCopyRequest duplicateCopyRequest) {
		validateDuplicateDocuments(duplicateCopyRequest);
//		validatePTSpecificNotNullFields(duplicateCopyRequest);
	}

}
