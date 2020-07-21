package org.egov.cpt.util;

import java.math.BigDecimal;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.SMSRequest;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

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
	 * @param application 
	 * 
	 * @param license
	 *            The tradeLicense for which message is to be sent
	 * @param localizationMessage
	 *            The messages from localisation
	 * @return customised message based on ownershipTransfer
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, Owner owner, String localizationMessage) {
		String message = null, messageTemplate;
		String ACTION_STATUS = owner.getApplicationAction() + "_" + owner.getApplicationState();
		
		switch (ACTION_STATUS) {
		
		case PTConstants.ACTION_STATUS_SUBMIT:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SUBMIT, localizationMessage);
			message = getInitiatedDcMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_REJECTED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_REJECTED, localizationMessage);
			message = getInitiatedDcMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_SENDBACK:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SENDBACK, localizationMessage);
			message = getInitiatedDcMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_APPROVED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_APPROVED, localizationMessage);
			message = getInitiatedDcMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_PAYMENT:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_PAYMENT, localizationMessage);
			message = getInitiatedDcMsg(owner, messageTemplate);
			break;
		}
		return message;
	}

	private String getInitiatedDcMsg(Owner owner, String message) {
		BigDecimal due = owner.getOwnerDetails().getDueAmount();
		BigDecimal charge = owner.getOwnerDetails().getAproCharge();
		message = message.replace("<2>", owner.getOwnerDetails().getName());
		message = message.replace("<3>", PTConstants.OWNERSHIP_TRANSFER_APPLICATION);
		message = message.replace("<4>", owner.getOwnerDetails().getApplicationNumber());
		message = message.replace("<5>",  (CharSequence) due.add(charge));
		return message;
	}

	private String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = null;
		try {
			Object messageObj = JsonPath.parse(localizationMessage).read(path);
			message = ((ArrayList<String>) messageObj).get(0);
		} catch (Exception e) {
//			log.warn("Fetching from localization failed", e);
			return ""+e;
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

	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo), requestInfo);
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
	}

	private StringBuilder getUri(String tenantId, RequestInfo requestInfo) {
		
		tenantId = tenantId.split("\\.")[0];
		
		String locale = PTConstants.NOTIFICATION_LOCALE;
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2) {
			locale = requestInfo.getMsgId().split("\\|")[1];
		}
		
		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(PTConstants.MODULE);
		
		return uri;
	}
	
	// Duplicate Copy Notifications

	public String getCustomizedDcMsg(RequestInfo requestInfo, DuplicateCopy copy, String localizationMessage) {

		String message = null, messageTemplate;
		String ACTION_STATUS = copy.getAction() + "_" + copy.getState();
		
		switch (ACTION_STATUS) {
		
		case PTConstants.ACTION_STATUS_SUBMIT:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SUBMIT, localizationMessage);
			message = getInitiatedDcMsg(copy, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_REJECTED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_REJECTED, localizationMessage);
			message = getInitiatedDcMsg(copy, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_SENDBACK:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SENDBACK, localizationMessage);
			message = getInitiatedDcMsg(copy, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_APPROVED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_APPROVED, localizationMessage);
			message = getInitiatedDcMsg(copy, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_PAYMENT:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_PAYMENT, localizationMessage);
			message = getInitiatedDcMsg(copy, messageTemplate);
			break;
		}
		return message;
	}

	private String getInitiatedDcMsg(DuplicateCopy copy, String message) {
		BigDecimal fee = copy.getApplicant().get(0).getFeeAmount();
		BigDecimal charge = copy.getApplicant().get(0).getAproCharge();
		message = message.replace("<2>", copy.getApplicant().get(0).getName());
		message = message.replace("<3>", PTConstants.DUPLICATE_COPY_APPLICATION);
		message = message.replace("<4>", copy.getApplicationNumber());
		if (message.contains("<5>")) {
			message = message.replace("<5>",  (CharSequence) fee.add(charge));
		}
		return message;
	}

	public String getOTPaymentMsg(Owner owner, String localizationMessages) {
		 String messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_PAYMENT_SUCCESS, localizationMessages);
		 messageTemplate = messageTemplate.replace("<2>", getMessageTemplate(owner.getOwnerDetails().getName(), localizationMessages));
		 messageTemplate = messageTemplate.replace("<4>", getMessageTemplate(owner.getOwnerDetails().getApplicationNumber(), localizationMessages));
		 messageTemplate = messageTemplate.replace("<3>", getMessageTemplate(PTConstants.OWNERSHIP_TRANSFER_APPLICATION, localizationMessages));
		 
		return messageTemplate;
	}

	public String getDCPaymentMsg(DuplicateCopy copy, String localizationMessages) {
		 String messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_PAYMENT_SUCCESS, localizationMessages);
		 messageTemplate = messageTemplate.replace("<2>", getMessageTemplate(copy.getApplicant().get(0).getName(), localizationMessages));
		 messageTemplate = messageTemplate.replace("<4>", getMessageTemplate(copy.getApplicationNumber(), localizationMessages));
		 messageTemplate = messageTemplate.replace("<3>", getMessageTemplate(PTConstants.DUPLICATE_COPY_APPLICATION, localizationMessages));
		 
		return messageTemplate;
	}
	
	
	//Mortgage Notifications

	public String getCustomizedMGMsg(RequestInfo requestInfo, Mortgage mortgage, String localizationMessage) {
		String message = null, messageTemplate;
		String ACTION_STATUS = mortgage.getAction() + "_" + mortgage.getState();
		
switch (ACTION_STATUS) {
		
		case PTConstants.ACTION_STATUS_SUBMIT:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SUBMIT, localizationMessage);
			message = getInitiatedMGMsg(mortgage, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_REJECTED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_REJECTED, localizationMessage);
			message = getInitiatedMGMsg(mortgage, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_SENDBACK:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SENDBACK, localizationMessage);
			message = getInitiatedMGMsg(mortgage, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_MORTGAGE_APPROVED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_APPROVED, localizationMessage);
			message = getInitiatedMGMsg(mortgage, messageTemplate);
			break;
			
		}
		return message;
	}

	private String getInitiatedMGMsg(Mortgage mortgage, String message) {
		message = message.replace("<2>", mortgage.getApplicant().get(0).getName());
		message = message.replace("<3>", PTConstants.MORTGAGE_APPLICATION);
		message = message.replace("<4>", mortgage.getApplicationNumber());
		
		return message;
	}

}
