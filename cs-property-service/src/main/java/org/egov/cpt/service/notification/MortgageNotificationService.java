package org.egov.cpt.service.notification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.EmailRequest;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.NotificationUtil;
import org.egov.cpt.web.contracts.MortgageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MortgageNotificationService {

	private PropertyConfiguration config;

	private ServiceRequestRepository serviceRequestRepository;

	private NotificationUtil util;

	@Autowired
	public MortgageNotificationService(PropertyConfiguration config, ServiceRequestRepository serviceRequestRepository,
			NotificationUtil util) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.util = util;
	}

	public void process(MortgageRequest request) {

		List<SMSRequest> smsRequestsProperty = new LinkedList<>();
		List<EmailRequest> emailRequest = new LinkedList<>();

		if (config.getIsSMSNotificationEnabled() != null) {
			if (config.getIsSMSNotificationEnabled()) {
				enrichSMSRequest(request, smsRequestsProperty);
				if (!CollectionUtils.isEmpty(smsRequestsProperty)) {
					util.sendSMS(smsRequestsProperty, true);
				}
			}
		}
		if(null != config.getIsEMAILNotificationEnabled()) {
			if(config.getIsEMAILNotificationEnabled()) {
				enrichEMAILRequest(request,emailRequest);
				if(!CollectionUtils.isEmpty(emailRequest))
					util.sendEMAIL(emailRequest,true);
			}
		}

	}

	private void enrichEMAILRequest(MortgageRequest request, List<EmailRequest> emailRequest) {
		 String tenantId = request.getMortgageApplications().get(0).getTenantId();
	        for(Mortgage mortgage : request.getMortgageApplications()){
	        	Map<String,String > emailIdToApplicant = new HashMap<>();
	            
	        	mortgage.getApplicant().forEach(applicant -> {
	                if(applicant.getEmail()!= null)
	                	emailIdToApplicant.put(applicant.getEmail(),applicant.getName());
	            });
	            if (emailIdToApplicant.isEmpty()) {
	            	continue;
	            }
				String message = null;
				String localizationMessages;
						localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
						message = util.getCustomizedMGMsg(request.getRequestInfo(), mortgage, localizationMessages);
	            if(message==null) continue;

				message = message.replace("\\n", "\n");
				emailRequest.addAll(util.createEMAILRequest(message,emailIdToApplicant));
	        }

	}

	private void enrichSMSRequest(MortgageRequest request, List<SMSRequest> smsRequests) {
		String tenantId = request.getMortgageApplications().get(0).getTenantId();
		for (Mortgage mortgage : request.getMortgageApplications()) {
			String message = null;
			String localizationMessages;

			localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
			message = util.getCustomizedMGMsg(request.getRequestInfo(), mortgage, localizationMessages);

			if (message == null)
				continue;

			Map<String, String> mobileNumberToOwner = new HashMap<>();

			if (mortgage.getApplicant().get(0).getPhone() != null) {
				mobileNumberToOwner.put(mortgage.getApplicant().get(0).getPhone(),
						mortgage.getApplicant().get(0).getName());
			}
			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		}

	}

}
