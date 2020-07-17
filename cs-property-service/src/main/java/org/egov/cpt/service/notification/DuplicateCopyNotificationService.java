package org.egov.cpt.service.notification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.NotificationUtil;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DuplicateCopyNotificationService {

	private PropertyConfiguration config;

	private ServiceRequestRepository serviceRequestRepository;

	private NotificationUtil util;

	@Autowired
	public DuplicateCopyNotificationService(PropertyConfiguration config,
			ServiceRequestRepository serviceRequestRepository, NotificationUtil util) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.util = util;
	}

	public void process(DuplicateCopyRequest request) {

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

	private void enrichSMSRequest(DuplicateCopyRequest request, List<SMSRequest> smsRequests) {
		String tenantId = request.getDuplicateCopyApplications().get(0).getTenantId();
		for (DuplicateCopy copy : request.getDuplicateCopyApplications()) {
			String message = null;
			String localizationMessages;

			localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
			message = util.getCustomizedDcMsg(request.getRequestInfo(), copy, localizationMessages);

			if (message == null)
				continue;

			Map<String, String> mobileNumberToOwner = new HashMap<>();

			if (copy.getApplicant().get(0).getPhone() != null) {
				mobileNumberToOwner.put(copy.getApplicant().get(0).getPhone(), copy.getApplicant().get(0).getName());
			}
			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		}

	}

}
