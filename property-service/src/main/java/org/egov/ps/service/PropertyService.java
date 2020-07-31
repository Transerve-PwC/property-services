package org.egov.ps.service;

import java.util.List;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.Property;
import org.egov.ps.producer.Producer;
import org.egov.ps.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private Configuration config;

	@Autowired
	private Producer producer;

	public List<Property> createProperty(PropertyRequest request) {

//		propertyValidator.validateCreateRequest(request);
		enrichmentService.enrichCreateRequest(request);
//		if (config.getIsWorkflowEnabled()) {
//			wfIntegrator.callWorkFlow(request);
//		}
		producer.push(config.getSavePropertyTopic(), request);
		return request.getProperties();
	}

}
