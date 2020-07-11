package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.Property;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.MortgageRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MortgageService {
	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private EnrichmentService enrichmentService;
	
	@Autowired
	private PropertyConfiguration config;
	
	@Autowired
	private Producer producer;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	public List<Mortgage> createApplication(MortgageRequest mortgageRequest) {
		List<Property> propertiesFromDb= propertyValidator.isPropertyExist(mortgageRequest);
		propertyValidator.validateMortgageCreateRequest(mortgageRequest); 
		enrichmentService.enrichMortgageCreateRequest(mortgageRequest);
//		propertyValidator.validateMortgageCreate(mortgageRequest);
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callMortgageWorkFlow(mortgageRequest);
		}
//		producer.push(config.getSaveMortgageTopic(), mortgageRequest);
		return mortgageRequest.getMortgageApplications();
	}

	public List<Mortgage> searchApplication(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		propertyValidator.validateDuplicateCopySearch(requestInfo,criteria);
//	    enrichmentService.enrichDuplicateCopySearchCriteria(requestInfo,criteria);
		List<Mortgage> properties = getApplication(criteria, requestInfo);
		return properties;
	}

	private List<Mortgage> getApplication(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		List<Mortgage> application = repository.getMortgageProperties(criteria);
        if(application.isEmpty())
            return Collections.emptyList();
        return application;
	}
}
