package org.egov.cpt.util;

//import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Component
@Slf4j
public class NotificationUtil {
	
	private PropertyConfiguration config;
	
	private ServiceRequestRepository serviceRequestRepository;
	
	private Producer producer;
	
	@Autowired
	public NotificationUtil(PropertyConfiguration config, ServiceRequestRepository serviceRequestRepository, Producer producer) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.producer = producer;
	}
	
	final String receiptNumberKey = "receiptNumber";

	final String amountPaidKey = "amountPaid";
	
	final String consumerCodeKey = "consumerCodeKey";
	
	/**
	 * Creates customised message based on ownershipTransfer
	 * @param applicationState 
	 * @param applicationAction 
	 * @param application 
	 * 
	 * @param license
	 *            The tradeLicense for which message is to be sent
	 * @param localizationMessage
	 *            The messages from localisation
	 * @return customised message based on ownershipTransfer
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, Owner owner, String applicationNumber, String applicationState, String applicationAction) {
		String message = null;
		
		if (applicationState != null && applicationAction != null) {
			if (applicationAction.equals(PTConstants.ACTION_OT_SUBMIT) && applicationState.equals(PTConstants.STATE_OT_INITIATED)) {
				message = PTConstants.NOTIFICATION_OT_CREATE;
				
				message = message.replace("{1}", owner.getOwnerDetails().getName());
				message = message.replace("{2}", owner.getOwnerDetails().getApplicationNumber());
				
				return message;
			} else if (applicationAction.contains(PTConstants.ACTION_OT_REJECT)){
				message = PTConstants.NOTIFICATION_OT_REJECT;
				
				message = message.replace("{1}", owner.getOwnerDetails().getName());
				message = message.replace("{2}", owner.getOwnerDetails().getApplicationNumber());
				
				return message;
			} else if (applicationAction.equals(PTConstants.ACTION_OT_SENDBACK) && applicationState.equals(PTConstants.STATE_OT_PENDING_CLARIFICATION)){ 
				message = PTConstants.NOTIFICATION_OT_SENDBACK;
				
				message = message.replace("{1}", owner.getOwnerDetails().getName());
				message = message.replace("{2}", owner.getOwnerDetails().getApplicationNumber());
				
				return message;
			} else if (applicationAction.contains(PTConstants.ACTION_OT_PAY) &&applicationState.contains(PTConstants.STATE_OT_APPROVED)){
				message = PTConstants.NOTIFICATION_OT_APPROVE;
				
				message = message.replace("{1}", owner.getOwnerDetails().getName());
				message = message.replace("{2}", owner.getOwnerDetails().getApplicationNumber());
				
				return message;
			}
		}
		return message;
	}

	/**
	 * Creates sms request for the each owners
	 * 
	 * @param message
	 *            The message for the specific ownershipTransfer
	 * @param mobileNumberToOwnerName
	 *            Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwner) {
		List<SMSRequest> smsRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToOwner.entrySet()) {
			String customizedMsg = message.replace("<1>", entryset.getValue());
			smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
		}
		return smsRequest;
	}

	public void sendSMS(List<SMSRequest> smsRequestsList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestsList)) {
//				log.info("Messages from localization couldn't be fetched!");
			}
			for (SMSRequest smsRequest : smsRequestsList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
//				log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
			}
		}
		
	}

}
