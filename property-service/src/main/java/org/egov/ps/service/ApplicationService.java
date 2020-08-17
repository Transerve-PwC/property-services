package org.egov.ps.service;

import java.util.List;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.producer.Producer;
import org.egov.ps.validator.ApplicationValidatorService;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public List<Application> createApplication(ApplicationRequest request) {
		validator.validateCreateRequest(request);
		// enrichmentService.enrichCreateApplication(request);
		// producer.push(config.getSaveApplicationTopic(), request);
		return request.getApplications();
	}
}
