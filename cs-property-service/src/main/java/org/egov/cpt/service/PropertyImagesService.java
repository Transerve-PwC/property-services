package org.egov.cpt.service;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.PropertyImages;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.service.calculation.DemandService;
import org.egov.cpt.service.notification.DuplicateCopyNotificationService;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.PropertyImagesRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PropertyImagesService {

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
	private DemandService demandService;

	@Autowired
	private DuplicateCopyNotificationService notificationService;

	public List<PropertyImages> createPropertyImages(PropertyImagesRequest propertyImagesRequest) {
		propertyValidator.isPropertyPIExist(propertyImagesRequest);
//		propertyValidator.validateDuplicateCopyCreateRequest(propertyImagesRequest);
		enrichmentService.enrichpropertyImageCreateRequest(propertyImagesRequest);
//		demandService.createDuplicateCopyDemand(propertyImagesRequest.getRequestInfo(),
//				propertyImagesRequest.getPropertyImagesApplications());
//		propertyValidator.validateDuplicateCreate(propertyImagesRequest);
//		if (config.getIsWorkflowEnabled()) {
//			wfIntegrator.callDuplicateCopyWorkFlow(propertyImagesRequest);
//		}
		producer.push(config.getSavePropertyImagesTopic(), propertyImagesRequest);
		return propertyImagesRequest.getPropertyImagesApplications();
	}

	public List<DuplicateCopy> searchPropertyImages(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		propertyValidator.validateDuplicateCopySearch(requestInfo,criteria);
	    enrichmentService.enrichDuplicateCopySearchCriteria(requestInfo,criteria);
		List<DuplicateCopy> properties = getApplication(criteria, requestInfo);
		return properties;
	}

	private List<DuplicateCopy> getApplication(DuplicateCopySearchCriteria criteria, RequestInfo requestInfo) {
		List<DuplicateCopy> application = repository.getDuplicateCopyProperties(criteria);
		if (application.isEmpty())
			return Collections.emptyList();
		return application;
	}

	public List<DuplicateCopy> updatePropertyImages(DuplicateCopyRequest duplicateCopyRequest) {

		List<DuplicateCopy> searchedProperty = propertyValidator
				.validateDuplicateCopyUpdateRequest(duplicateCopyRequest);
		enrichmentService.enrichDuplicateCopyUpdateRequest(duplicateCopyRequest, searchedProperty);
		String applicationState = duplicateCopyRequest.getDuplicateCopyApplications().get(0).getState();
		if (applicationState.equalsIgnoreCase(PTConstants.DC_STATE_PENDING_SA_VERIFICATION)) {
			demandService.updateDuplicateCopyDemand(duplicateCopyRequest.getRequestInfo(),
					duplicateCopyRequest.getDuplicateCopyApplications());
		}
		if (applicationState.equalsIgnoreCase(PTConstants.DC_STATE_PENDING_APRO)) {
			demandService.updateDuplicateCopyDemand(duplicateCopyRequest.getRequestInfo(),
					duplicateCopyRequest.getDuplicateCopyApplications());
		}
		propertyValidator.validateDuplicateUpdate(duplicateCopyRequest);
		if (config.getIsWorkflowEnabled()) {
			wfIntegrator.callDuplicateCopyWorkFlow(duplicateCopyRequest);
		}
		producer.push(config.getUpdateDuplicateCopyTopic(), duplicateCopyRequest);
		notificationService.process(duplicateCopyRequest);
		return duplicateCopyRequest.getDuplicateCopyApplications();
	}

}
