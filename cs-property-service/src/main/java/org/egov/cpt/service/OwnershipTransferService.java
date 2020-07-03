package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class OwnershipTransferService {

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private OwnershipTransferRepository repository;

	public List<Property> createOwnershipTransfer(PropertyRequest request) {
//		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		List<Property> propertyFromSearch = propertyValidator.getPropertyForOT(request);
		enrichmentService.enrichCreateOwnershipTransfer(request, propertyFromSearch);
//		userService.createUser(request); // TODO create user as owner of the property if does not exists
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callWorkFlow(request, "OT");
		}
		producer.push(config.getOwnershipTransferSaveTopic(), request);
		return request.getProperties();
	}

	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo) {

		List<Property> properties = null;
//		propertyValidator.validatePropertyCriteria(criteria, requestInfo);
//
//		if (criteria.getMobileNumber() != null || criteria.getName() != null || criteria.getOwnerIds() != null) {
//
//			Boolean shouldReturnEmptyList = enrichCriteriaFromUser(criteria, requestInfo);
//
//			if (shouldReturnEmptyList)
//				return Collections.emptyList();
//
//			properties = getPropertiesWithOwnerInfo(criteria, requestInfo);
//		} else {
//			properties = getPropertiesWithOwnerInfo(criteria, requestInfo);
//		}

		properties = getPropertiesWithOwnerInfo(criteria, requestInfo);
		return properties;
	}

	List<Property> getPropertiesWithOwnerInfo(PropertyCriteria criteria, RequestInfo requestInfo) {

		List<Property> properties = repository.getProperties(criteria);
		if (CollectionUtils.isEmpty(properties))
			return Collections.emptyList();

//		Set<String> ownerIds = properties.stream().map(Property::getOwner).flatMap(List::stream).map(Owner::getId)
//				.collect(Collectors.toSet());
//
//		UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(criteria.getTransit_number(),
//				requestInfo);
//		userSearchRequest.setId(ownerIds);
//
//		UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
//		enrichmentService.enrichOwner(userDetailResponse, properties);
		return properties;
	}

}
