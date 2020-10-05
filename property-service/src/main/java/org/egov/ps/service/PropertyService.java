package org.egov.ps.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.producer.Producer;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.validator.PropertyValidator;
import org.egov.ps.web.contracts.BusinessService;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.ps.web.contracts.State;
import org.egov.ps.workflow.WorkflowIntegrator;
import org.egov.ps.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PropertyService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private Configuration config;

	@Autowired
	private Producer producer;

	@Autowired
	PropertyValidator propertyValidator;

	@Autowired
	PropertyRepository repository;

	@Autowired
	WorkflowIntegrator wfIntegrator;

	@Autowired
	private WorkflowService workflowService;

	public List<Property> createProperty(PropertyRequest request) {
//		propertyValidator.validateCreateRequest(request);
		enrichmentService.enrichCreateRequest(request);
		producer.push(config.getSavePropertyTopic(), request);
		return request.getProperties();
	}

	/**
	 * Updates the property
	 *
	 * @param request PropertyRequest containing list of properties to be update
	 * @return List of updated properties
	 */
	public List<Property> updateProperty(PropertyRequest request) {
		List<Property> propertyFromSearch = propertyValidator.validateUpdateRequest(request);
		enrichmentService.enrichUpdateRequest(request, propertyFromSearch);
		String action = request.getProperties().get(0).getAction();
		if (config.getIsWorkflowEnabled() && !action.contentEquals("") && !action.contentEquals("DRAFT")) {
			wfIntegrator.callWorkFlow(request);
		}
		producer.push(config.getUpdatePropertyTopic(), request);

		return request.getProperties();
	}

	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo) {
		if (criteria.getFileNumber() != null) {
			criteria.setFileNumber(criteria.getFileNumber().toUpperCase());
		}

		if (criteria.isEmpty()) {
			BusinessService otBusinessService = workflowService.getBusinessService(PSConstants.TENENT_ID, requestInfo,
					config.getPsBusinessServiceValue());
			List<State> stateList = otBusinessService.getStates();
			List<String> states = new ArrayList<String>();

			for (State state : stateList) {
				states.add(state.getState());
			}
			states.remove("");
			criteria.setState(states);
			criteria.setUserId(requestInfo.getUserInfo().getUuid());
		} else if (criteria.getState() != null && criteria.getState().contains(PSConstants.PM_DRAFTED)) {
			criteria.setUserId(requestInfo.getUserInfo().getUuid());
		}

		List<Property> properties = repository.getProperties(criteria);

		if (CollectionUtils.isEmpty(properties))
			return Collections.emptyList();
		return properties;
	}

}
