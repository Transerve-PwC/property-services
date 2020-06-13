package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PropertyService {

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private UserService userService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private PropertyRepository repository;

	public List<Property> createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		enrichmentService.enrichCreateRequest(request); // TODO assign UUIDs for required fields
		userService.createUser(request); // TODO create user as owner of the property if does not exists
		if (config.getIsWorkflowEnabled()) // TODO initially it is false change it to true if want to add workflow
			updateWorkflow(request, true); // TODO as per my understanding true for create false for update method
		producer.push(config.getSavePropertyTopic(), request);
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

	private void updateWorkflow(PropertyRequest request, Boolean isCreate) {

//		Property property = request.getProperty();
//
//		ProcessInstanceRequest workflowReq = util.getWfForPropertyRegistry(request, isCreate);
//		String status = wfService.callWorkFlow(workflowReq);
//		if (status.equalsIgnoreCase(config.getWfStatusActive()) && property.getPropertyId() == null) {
//
//			String pId = enrichmentService.getIdList(request.getRequestInfo(), property.getTenantId(),
//					config.getPropertyIdGenName(), config.getPropertyIdGenFormat(), 1).get(0);
//			request.getProperty().setPropertyId(pId);
//		}
//		request.getProperty().setStatus(Status.fromValue(status));
	}
}
