package org.egov.cpt.service;

import java.util.List;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Property;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public List<Property> createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		enrichmentService.enrichCreateRequest(request); // TODO assign UUIDs for required fields
		userService.createUser(request); // TODO create user as owner of the property if does not exists
		if (config.getIsWorkflowEnabled()) // TODO initially it is false change it to true if want to add workflow
			updateWorkflow(request, true); // TODO as per my understanding true for create false for update method
		producer.push(config.getSavePropertyTopic(), request);
		return request.getProperties();
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
