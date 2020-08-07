package org.egov.cpt.service;

import java.util.List;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.NoticeGeneration;
import org.egov.cpt.models.Property;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.service.notification.MortgageNotificationService;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.MortgageRequest;
import org.egov.cpt.web.contracts.NoticeGenerationRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.egov.cpt.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoticeGenerationService {
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
	
	@Autowired
	MortgageNotificationService notificationService;
	
	@Autowired
	private WorkflowService workflowService;

	public List<NoticeGeneration> createNotice(NoticeGenerationRequest noticeGenerationRequest) {
		List<Property> propertiesFromDb = propertyValidator.isPropertyExist(noticeGenerationRequest);
//		propertyValidator.validateMortgageCreateRequest(noticeGenerationRequest);
		enrichmentService.enrichNoticeCreateRequest(noticeGenerationRequest);
		producer.push(config.getSaveNoticeTopic(), noticeGenerationRequest);
		return noticeGenerationRequest.getNoticeApplications();
	}

	public List<NoticeGeneration> updateNotice(NoticeGenerationRequest noticeGenerationRequest) {
		List<NoticeGeneration> searchedApplication = propertyValidator.validateNoticeUpdateRequest(noticeGenerationRequest); 
		enrichmentService.enrichNoticeUpdateRequest(noticeGenerationRequest,searchedApplication);
		propertyValidator.validateNoticeUpdate(noticeGenerationRequest);
		producer.push(config.getUpdateNoticeTopic(), noticeGenerationRequest);
		return noticeGenerationRequest.getNoticeApplications();
				
	}
}
