package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.service.calculation.DemandService;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
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

	@Autowired
	private DemandService demandService;

	public List<Owner> createOwnershipTransfer(OwnershipTransferRequest request) {
//		propertyValidator.validateCreateRequest(request); // TODO add validations as per requirement
		List<Property> propertyFromSearch = propertyValidator.getPropertyForOT(request);
		enrichmentService.enrichCreateOwnershipTransfer(request, propertyFromSearch);
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callOwnershipTransferWorkFlow(request);
		}
		producer.push(config.getOwnershipTransferSaveTopic(), request);
		return request.getOwners();
	}

	public List<Owner> searchOwnershipTransfer(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		List<Owner> owners = repository.searchOwnershipTransfer(criteria);
		if (CollectionUtils.isEmpty(owners))
			return Collections.emptyList();
		return owners;
	}

	public List<Owner> updateOwnershipTransfer(OwnershipTransferRequest request) {
		List<Owner> ownersFromSearch = propertyValidator.validateUpdateRequest(request);
		enrichmentService.enrichUpdateOwnershipTransfer(request, ownersFromSearch);
		String applicationState = request.getOwners().get(0).getApplicationState();
		if (applicationState.equalsIgnoreCase("PENDINGSAVERIFICATION") // demand generation
				|| applicationState.equalsIgnoreCase("PENDINGAPRO")) {
			demandService.generateDemand(request);
		}
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callOwnershipTransferWorkFlow(request);
		}
		producer.push(config.getOwnershipTransferUpdateTopic(), request);
		return request.getOwners();
	}

}
