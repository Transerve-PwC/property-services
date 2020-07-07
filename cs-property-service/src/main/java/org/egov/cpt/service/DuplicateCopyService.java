package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DuplicateCopyService {
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
	

	public List<DuplicateCopy> createApplication(DuplicateCopyRequest duplicateCopyRequest) {
		propertyValidator.isPropertyExist(duplicateCopyRequest);
		propertyValidator.validateDuplicateCopyCreateRequest(duplicateCopyRequest); 
		enrichmentService.enrichDuplicateCopyCreateRequest(duplicateCopyRequest);
		propertyValidator.validateDuplicateCreate(duplicateCopyRequest);
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callDuplicateCopyWorkFlow(duplicateCopyRequest);
		}
		producer.push(config.getSaveDuplicateCopyTopic(), duplicateCopyRequest);
		return duplicateCopyRequest.getDuplicateCopyApplications();
	}

	public List<DuplicateCopy> searchApplication(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		propertyValidator.validateDuplicateCopySearch(requestInfo,criteria);
//	    enrichmentService.enrichDuplicateCopySearchCriteria(requestInfo,criteria);
		List<DuplicateCopy> properties = getApplication(criteria, requestInfo);
		return properties;
	}

	private List<DuplicateCopy> getApplication(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		 List<DuplicateCopy> application = repository.getDuplicateCopyProperties(criteria);
	        if(application.isEmpty())
	            return Collections.emptyList();
	        return application;
	}

	public List<DuplicateCopy> updateApplication(DuplicateCopyRequest duplicateCopyRequest) {
		
		List<DuplicateCopy> searchedProperty = propertyValidator.validateDuplicateCopyUpdateRequest(duplicateCopyRequest); 
		enrichmentService.enrichDuplicateCopyUpdateRequest(duplicateCopyRequest,searchedProperty);
		propertyValidator.validateDuplicateUpdate(duplicateCopyRequest);
		if (config.getIsWorkflowEnabled()) {
            wfIntegrator.callDuplicateCopyWorkFlow(duplicateCopyRequest);
        } 
		producer.push(config.getUpdateDuplicateCopyTopic(), duplicateCopyRequest);
		return duplicateCopyRequest.getDuplicateCopyApplications();
	}

}
