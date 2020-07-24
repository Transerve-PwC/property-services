package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.State;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.service.calculation.DemandService;
import org.egov.cpt.service.notification.PropertyNotificationService;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.egov.cpt.workflow.WorkflowService;
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

	@Autowired
	private DemandService demandService;
	
	@Autowired
	private WorkflowService workflowService;

	public List<Owner> createOwnershipTransfer(OwnershipTransferRequest request) {
//		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		List<Property> propertyFromSearch = propertyValidator.getPropertyForOT(request);
		enrichmentService.enrichCreateOwnershipTransfer(request, propertyFromSearch);
		demandService.createDemand(request.getRequestInfo(), request.getOwners());
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callOwnershipTransferWorkFlow(request);
		}
		producer.push(config.getOwnershipTransferSaveTopic(), request);
		return request.getOwners();
	}

	public List<Owner> searchOwnershipTransfer(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		if (criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN")) {
			criteria.setApplicantMobNo(requestInfo.getUserInfo().getUserName());
		}
		if(requestInfo.getUserInfo().getType().equalsIgnoreCase("EMPLOYEE")&& CollectionUtils.isEmpty(criteria.getStatus())){
			String wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_OT;
			BusinessService otBusinessService = workflowService.getBusinessService("ch", requestInfo, wfbusinessServiceName);
			List<State> stateList= otBusinessService.getStates();
			List<String> states = new ArrayList<String>();
			
			for(State state: stateList){
					states.add(state.getState());
			}
			states.remove("");
			if(states.contains("OT_DRAFTED"))
				states.remove("OT_DRAFTED");
			
			criteria.setStatus(states);
		}
		List<Owner> owners = repository.searchOwnershipTransfer(criteria);

		if (CollectionUtils.isEmpty(owners))
			return Collections.emptyList();

		return owners;
	}

	public List<Owner> updateOwnershipTransfer(OwnershipTransferRequest request) {
		List<Owner> ownersFromSearch = propertyValidator.validateUpdateRequest(request);
		enrichmentService.enrichUpdateOwnershipTransfer(request, ownersFromSearch);
		String applicationState = request.getOwners().get(0).getApplicationState(); // demand generation
		if (applicationState.equalsIgnoreCase(PTConstants.OT_STATE_PENDING_SA_VERIFICATION)) {
			demandService.updateDemand(request.getRequestInfo(), request.getOwners());
		}
		if (applicationState.equalsIgnoreCase(PTConstants.OT_STATE_PENDING_APRO)) {
			demandService.updateDemand(request.getRequestInfo(), request.getOwners());
		}
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callOwnershipTransferWorkFlow(request);
		}
		producer.push(config.getOwnershipTransferUpdateTopic(), request);
//		propertyNotificationService.process(request);
		return request.getOwners();
	}

}
