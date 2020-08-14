package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.State;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.egov.cpt.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
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

	@Autowired
	private WorkflowIntegrator wfIntegrator;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private RentEnrichmentService rentEnrichmentService;

	public List<Property> createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		enrichmentService.enrichCreateRequest(request);
		userService.createUser(request); // TODO create user as owner of the property if does not exists
		/*if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callWorkFlow(request);
		}*/
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
		rentEnrichmentService.enrichDemandsPayments(request);
		userService.createUser(request);
		if (config.getIsWorkflowEnabled() && !request.getProperties().get(0).getMasterDataAction().equalsIgnoreCase("")) {
			wfIntegrator.callWorkFlow(request);
		}
		producer.push(config.getUpdatePropertyTopic(), request);
		return request.getProperties();
	}

	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo) {
		if(criteria.isEmpty()){
			String wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_PM;
			BusinessService otBusinessService = workflowService.getBusinessService(criteria.getTenantId(), requestInfo, wfbusinessServiceName);
			List<State> stateList= otBusinessService.getStates();
			List<String> states = new ArrayList<String>();
			
			for(State state: stateList){
					states.add(state.getState());
			}
			states.remove("");
			states.remove(PTConstants.PM_DRAFTED);
			log.info("states:"+states);
			criteria.setState(states);
			criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		}
		else{
			if(!CollectionUtils.isEmpty(criteria.getState())&&criteria.getState().contains(PTConstants.PM_DRAFTED))
				criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		}
		
		List<Property> properties = repository.getProperties(criteria);
		if (CollectionUtils.isEmpty(properties))
			return Collections.emptyList();
		return properties;
	}
}
