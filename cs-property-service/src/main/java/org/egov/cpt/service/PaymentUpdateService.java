package org.egov.cpt.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.PaymentDetail;
import org.egov.cpt.models.calculation.PaymentRequest;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.egov.cpt.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentUpdateService {

	private OwnershipTransferService ownershipTransferService;

	private PropertyConfiguration config;

	private OwnershipTransferRepository repository;

	private WorkflowIntegrator wfIntegrator;

	private EnrichmentService enrichmentService;

	private ObjectMapper mapper;

	private WorkflowService workflowService;

	private PropertyUtil util;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromPropertyTypeOnly;

	@Value("${egov.allowed.businessServices}")
	private String allowedBusinessServices;

	@Autowired
	public PaymentUpdateService(OwnershipTransferService ownershipTransferService, PropertyConfiguration config,
			OwnershipTransferRepository repository, WorkflowIntegrator wfIntegrator,
			EnrichmentService enrichmentService, ObjectMapper mapper, WorkflowService workflowService,
			PropertyUtil util) {
		this.ownershipTransferService = ownershipTransferService;
		this.config = config;
		this.repository = repository;
		this.wfIntegrator = wfIntegrator;
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
					DuplicateCopySearchCriteria searchCriteria = new DuplicateCopySearchCriteria();
					searchCriteria.setApplicationNumber(paymentDetail.getBill().getConsumerCode());

					List<Owner> owners = ownershipTransferService.searchOwnershipTransfer(searchCriteria, requestInfo);

					BusinessService businessService = workflowService.getBusinessService(owners.get(0).getTenantId(),
							requestInfo, "OwnershipTransferRP");

					if (CollectionUtils.isEmpty(owners))
						throw new CustomException("INVALID RECEIPT",
								"No Owner found for the comsumerCode " + searchCriteria.getApplicationNumber());

					owners.forEach(license -> license.setApplicationAction(PTConstants.ACTION_PAY));

					// FIXME check if the update call to repository can be avoided
					// FIXME check why aniket is not using request info from consumer
					// REMOVE SYSTEM HARDCODING AFTER ALTERING THE CONFIG IN WF FOR RP

					Role role = Role.builder().code("SYSTEM_PAYMENT").build();
					requestInfo.getUserInfo().getRoles().add(role);
					OwnershipTransferRequest updateRequest = OwnershipTransferRequest.builder().requestInfo(requestInfo)
							.owners(owners).build();

					/*
					 * calling workflow to update status
					 */
//					wfIntegrator.callOwnershipTransferWorkFlow(updateRequest);

					updateRequest.getOwners().forEach(
							obj -> log.info(" the status of the application is : " + obj.getApplicationState()));

					List<String> endStates = Collections.nCopies(updateRequest.getOwners().size(),
							PTConstants.STATUS_APPROVED);

					enrichmentService.postStatusEnrichment(updateRequest, endStates);

					/*
					 * calling repository to update the object in RP tables
					 */
					Map<String, Boolean> idToIsStateUpdatableMap = util.getIdToIsStateUpdatableMap(businessService,
							owners);
					repository.update(updateRequest, idToIsStateUpdatableMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Extracts the required fields as map
	 * 
	 * @param context The documentcontext of the incoming receipt
	 * @return Map containing values of required fields
	 */
	private Map<String, String> enrichValMap(DocumentContext context) {
		Map<String, String> valMap = new HashMap<>();
		try {
			valMap.put(businessService,
					context.read("$.Payments.*.paymentDetails[?(@.businessService=='TL')].businessService"));
			valMap.put(consumerCode,
					context.read("$.Payments.*.paymentDetails[?(@.businessService=='TL')].bill.consumerCode"));
			valMap.put(tenantId, context.read("$.Payments[0].tenantId"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("PAYMENT ERROR", "Unable to fetch values from payment");
		}
		return valMap;
	}

}
