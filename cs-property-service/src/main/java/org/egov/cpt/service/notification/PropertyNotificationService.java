package org.egov.cpt.service.notification;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.NotificationUtil;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyNotificationService {

	private PropertyConfiguration config;

	private ServiceRequestRepository serviceRequestRepository;

	private NotificationUtil util;

	@Autowired
	public PropertyNotificationService(PropertyConfiguration config, ServiceRequestRepository serviceRequestRepository,
			NotificationUtil util) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.util = util;
	}

	/**
	 * Creates and send the sms based on the OwnershipTransferRequest
	 * 
	 * @param request The OwnershipTransferRequest listenend on the kafka topic
	 */
	public void process(OwnershipTransferRequest request) {

		List<SMSRequest> smsRequestsProperty = new LinkedList<>();

		if (config.getIsSMSNotificationEnabled() != null) {
			if (config.getIsSMSNotificationEnabled()) {
				enrichSMSRequest(request, smsRequestsProperty);
				if (!CollectionUtils.isEmpty(smsRequestsProperty)) {
					util.sendSMS(smsRequestsProperty, true);
				}
			}

		}

	}

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param request     The OwnershipTransferRequest from kafka topic
	 * @param smsRequests List of SMSRequets
	 */
	private void enrichSMSRequest(OwnershipTransferRequest request, List<SMSRequest> smsRequests) {
		String tenantId = request.getOwners().get(0).getTenantId();
		for (Owner owner : request.getOwners()) {
			String message = null;
			String localizationMessages;
			
			localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
			message = util.getCustomizedMsg(request.getRequestInfo(), owner, localizationMessages);
			
			if (message == null) continue;
			
			Map<String, String> mobileNumberToOwner = new HashMap<>();

			if (owner.getOwnerDetails().getPhone() != null) {
				mobileNumberToOwner.put(owner.getOwnerDetails().getPhone(), owner.getOwnerDetails().getName());
			}
			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		}

	}

}
