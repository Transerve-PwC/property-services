package org.egov.cpt.service.notification;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.PaymentDetail;
import org.egov.cpt.models.calculation.PaymentRequest;
import org.egov.cpt.repository.OwnershipTransferRepository;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.service.DuplicateCopyService;
import org.egov.cpt.service.EnrichmentService;
import org.egov.cpt.service.OwnershipTransferService;
import org.egov.cpt.util.NotificationUtil;
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

import io.micrometer.core.instrument.MeterRegistry.Config;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentNotificationService {

	private OwnershipTransferService ownershipTransferService;

	private OwnershipTransferRepository repositoryOt;

	private DuplicateCopyService duplicateCopyService;

	private PropertyRepository propertyRepository;

	private EnrichmentService enrichmentService;

	private ObjectMapper mapper;

	private WorkflowService workflowService;

	private NotificationUtil util;
	
	private PropertyConfiguration config;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromPropertyTypeOnly;

	@Value("${egov.allowed.businessServices}")
	private String allowedBusinessServices;

	@Autowired
	public PaymentNotificationService(OwnershipTransferService ownershipTransferService,
			OwnershipTransferRepository repositoryOt, DuplicateCopyService duplicateCopyService,
			PropertyRepository propertyRepository, EnrichmentService enrichmentService, ObjectMapper mapper,
			WorkflowService workflowService, NotificationUtil util, PropertyConfiguration config) {
		this.ownershipTransferService = ownershipTransferService;
		this.repositoryOt = repositoryOt;
		this.duplicateCopyService = duplicateCopyService;
		this.propertyRepository = propertyRepository;
		this.enrichmentService = enrichmentService;
		this.mapper = mapper;
		this.workflowService = workflowService;
		this.util = util;
		this.config = config;
	}

	final String tenantId = "tenantId";

	final String businessService = "businessService";

	final String consumerCode = "consumerCode";
	
	final String mobileKey = "mobileKey";

	Map<String, String> valMap = new HashMap<>();
	
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
					
					valMap.put(mobileKey, paymentDetail.getBill().getMobileNumber());

					String wfbusinessServiceName = null;
					switch (paymentDetail.getBusinessService()) {
					case PTConstants.BUSINESS_SERVICE_OT:
						wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_OT;

						DuplicateCopySearchCriteria searchCriteria = new DuplicateCopySearchCriteria();
						searchCriteria.setApplicationNumber(paymentDetail.getBill().getConsumerCode());

						List<Owner> owners = ownershipTransferService.searchOwnershipTransfer(searchCriteria,
								requestInfo);
						owners.forEach(owner ->{
							String localizationMessages = util.getLocalizationMessages(owner.getTenantId(), requestInfo);
							 List<SMSRequest> smsRequests = getCTLSMSRequests(owner, localizationMessages);
							 util.sendSMS(smsRequests, config.getIsSMSNotificationEnabled());
						});

						if (CollectionUtils.isEmpty(owners))
							throw new CustomException("INVALID RECEIPT",
									"No Owner found for the comsumerCode " + searchCriteria.getApplicationNumber());
						
						break;

					case PTConstants.BUSINESS_SERVICE_DC:
						wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_DC;

						DuplicateCopySearchCriteria searchCriteriaDc = new DuplicateCopySearchCriteria();
						searchCriteriaDc.setApplicationNumber(paymentDetail.getBill().getConsumerCode());

						List<DuplicateCopy> dcApplications = duplicateCopyService.searchApplication(searchCriteriaDc,
								requestInfo);

						dcApplications.forEach(copy -> {
							String localizationMessages = util.getLocalizationMessages(copy.getTenantId(), requestInfo);
							 List<SMSRequest> smsRequests = getDCSMSRequests(copy, localizationMessages);
							 util.sendSMS(smsRequests, config.getIsSMSNotificationEnabled());
						});

						if (CollectionUtils.isEmpty(dcApplications))
							throw new CustomException("INVALID RECEIPT",
									"No Owner found for the comsumerCode " + searchCriteriaDc.getApplicationNumber());

						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<SMSRequest> getDCSMSRequests(DuplicateCopy copy, String localizationMessages) {
		
		SMSRequest payerSmsRequest = getDCSMSRequest(copy, localizationMessages);
		 
        List<SMSRequest> totalSMS = new LinkedList<>();
        totalSMS.add(payerSmsRequest);
		 
		return totalSMS;
	}

	private SMSRequest getDCSMSRequest(DuplicateCopy copy, String localizationMessages) {
		String message = util.getDCPaymentMsg(copy, localizationMessages);
		 SMSRequest smsRequest = new SMSRequest(valMap.get(mobileKey), message);
		return smsRequest;
	}

	private List<SMSRequest> getCTLSMSRequests(Owner owner,
			String localizationMessages) {
		 SMSRequest payerSmsRequest = getOTSMSRequest(owner, localizationMessages);
		 
         List<SMSRequest> totalSMS = new LinkedList<>();
         totalSMS.add(payerSmsRequest);
		 
		return totalSMS;
	}

	private SMSRequest getOTSMSRequest(Owner owner,
			String localizationMessages) {
		 String message = util.getOTPaymentMsg(owner, localizationMessages);
		 SMSRequest smsRequest = new SMSRequest(valMap.get(mobileKey), message);
		return smsRequest;
	}

}
