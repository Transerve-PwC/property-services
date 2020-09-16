package org.egov.ps.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.model.ApplicationCriteria;
import org.egov.ps.producer.Producer;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.validator.ApplicationValidatorService;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ApplicationService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private Configuration config;

	@Autowired
	private Producer producer;

	@Autowired
	ApplicationValidatorService validator;
	
	@Autowired
	PropertyRepository repository;
	
	@Autowired
	WorkflowIntegrator wfIntegrator;

	public List<Application> createApplication(ApplicationRequest request) {
//		validator.validateCreateRequest(request);
		enrichmentService.enrichCreateApplication(request);
		producer.push(config.getSaveApplicationTopic(), request);
		return request.getApplications();
	}

	public List<Application> searchApplication(ApplicationCriteria criteria, RequestInfo requestInfo) {
		List<Application> applications = repository.getApplications(criteria);

		if (CollectionUtils.isEmpty(applications))
			return Collections.emptyList();
		return applications;
	}

	public List<Application> updateApplication(ApplicationRequest applicationRequest) {
		validator.getApplications(applicationRequest);
		enrichmentService.enrichUpdateApplication(applicationRequest);
		String action = applicationRequest.getApplications().get(0).getAction();
		if (config.getIsWorkflowEnabled() && !action.contentEquals("")) {
			wfIntegrator.callApplicationWorkFlow(applicationRequest);
		}
		producer.push(config.getUpdateApplicationTopic(), applicationRequest);

		return applicationRequest.getApplications();
	}
}
