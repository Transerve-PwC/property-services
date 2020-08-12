package org.egov.ps.service;

import java.util.List;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.producer.Producer;
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

	public List<Application> createProperty(ApplicationRequest request) {
		enrichmentService.enrichCreateApplication(request);
		producer.push(config.getSaveApplicationTopic(), request);
		return request.getApplications();
	}
}
