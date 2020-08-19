package org.egov.cpt.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.PaymentDetail;
import org.egov.cpt.models.calculation.PaymentRequest;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentUpdateService {

	private OwnershipTransferService ownershipTransferService;

	private OwnershipTransferRepository repositoryOt;

	private DuplicateCopyService duplicateCopyService;

	private PropertyRepository propertyRepository;

	private EnrichmentService enrichmentService;

	private ObjectMapper mapper;

	private WorkflowService workflowService;

	private PropertyUtil util;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromPropertyTypeOnly;

	@Value("${egov.allowed.businessServices}")
	private String allowedBusinessServices;

	@Autowired
	public PaymentUpdateService(OwnershipTransferService ownershipTransferService,
			OwnershipTransferRepository repositoryOt, DuplicateCopyService duplicateCopyService,
			PropertyRepository propertyRepository, EnrichmentService enrichmentService, ObjectMapper mapper,
			WorkflowService workflowService, PropertyUtil util) {
		this.ownershipTransferService = ownershipTransferService;
		this.repositoryOt = repositoryOt;
		this.duplicateCopyService = duplicateCopyService;
		this.propertyRepository = propertyRepository;
		this.enrichmentService = enrichmentService;
		this.mapper = mapper;
		this.workflowService = workflowService;
		this.util = util;
	}

	final String tenantId = "tenantId";

	final String businessService = "businessService";

	final String consumerCode = "consumerCode";

	/**
	 * Process the message from kafka and updates the status to paid
	 * 
	 * @param record The incoming message from receipt create consumer
	 */
	public void process(HashMap<String, Object> record) {

		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			RequestInfo requestInfo = paymentRequest.getRequestInfo();
			List<PaymentDetail> paymentDetails = paymentRequest.getPayment().getPaymentDetails();
			List<String> allowedservices = Arrays.asList(allowedBusinessServices.split(","));
			for (PaymentDetail paymentDetail : paymentDetails) {
				if (allowedservices.contains(paymentDetail.getBusinessService())) {

					String wfbusinessServiceName = null;
					switch (paymentDetail.getBusinessService()) {
					case PTConstants.BUSINESS_SERVICE_OT:
						wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_OT;

						DuplicateCopySearchCriteria searchCriteria = new DuplicateCopySearchCriteria();
						searchCriteria.setApplicationNumber(paymentDetail.getBill().getConsumerCode());

						List<Owner> owners = ownershipTransferService.searchOwnershipTransfer(searchCriteria,
								requestInfo);

						BusinessService otBusinessService = workflowService
								.getBusinessService(owners.get(0).getTenantId(), requestInfo, wfbusinessServiceName);

						if (CollectionUtils.isEmpty(owners))
							throw new CustomException("INVALID RECEIPT",
									"No Owner found for the comsumerCode " + searchCriteria.getApplicationNumber());

						owners.forEach(owner -> owner.setApplicationAction(PTConstants.ACTION_PAY));

						Role role = Role.builder().code("SYSTEM_PAYMENT").build();
						requestInfo.getUserInfo().getRoles().add(role);
						OwnershipTransferRequest updateRequest = OwnershipTransferRequest.builder()
								.requestInfo(requestInfo).owners(owners).build();

						updateRequest.getOwners().forEach(
								obj -> log.info(" the status of the application is : " + obj.getApplicationState()));

						List<String> endStates = Collections.nCopies(updateRequest.getOwners().size(),
								PTConstants.STATUS_APPROVED);

//						enrichmentService.postStatusEnrichment(updateRequest, endStates);

						Map<String, Boolean> idToIsStateUpdatableMap = util
								.getIdToIsStateUpdatableMap(otBusinessService, owners);

						repositoryOt.update(updateRequest, idToIsStateUpdatableMap);
						break;

					case PTConstants.BUSINESS_SERVICE_DC:
						wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_DC;

						DuplicateCopySearchCriteria searchCriteriaDc = new DuplicateCopySearchCriteria();
						searchCriteriaDc.setApplicationNumber(paymentDetail.getBill().getConsumerCode());

						List<DuplicateCopy> dcApplications = duplicateCopyService.searchApplication(searchCriteriaDc,
								requestInfo);

						BusinessService dcBusinessService = workflowService.getBusinessService(
								dcApplications.get(0).getTenantId(), requestInfo, wfbusinessServiceName);

						if (CollectionUtils.isEmpty(dcApplications))
							throw new CustomException("INVALID RECEIPT",
									"No Owner found for the comsumerCode " + searchCriteriaDc.getApplicationNumber());

						dcApplications.forEach(dcApplication -> dcApplication.setAction(PTConstants.ACTION_PAY));

						DuplicateCopyRequest updateDCRequest = DuplicateCopyRequest.builder().requestInfo(requestInfo)
								.duplicateCopyApplications(dcApplications).build();

						updateDCRequest.getDuplicateCopyApplications()
								.forEach(obj -> log.info(" the status of the application is : " + obj.getState()));

						List<String> dcEndStates = Collections.nCopies(
								updateDCRequest.getDuplicateCopyApplications().size(), PTConstants.STATUS_APPROVED);

//						enrichmentService.postStatusEnrichmentDC(updateDCRequest, dcEndStates);

						Map<String, Boolean> idToIsStateUpdatableMapDc = util
								.getIdToIsStateUpdatableMapDc(dcBusinessService, dcApplications);

						propertyRepository.updateDcPayment(updateDCRequest, idToIsStateUpdatableMapDc);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
