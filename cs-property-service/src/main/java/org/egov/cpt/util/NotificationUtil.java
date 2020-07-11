package org.egov.cpt.util;

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
			message = getInitiatedMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_REJECTED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_REJECTED, localizationMessage);
			message = getInitiatedMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_SENDBACK:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_SENDBACK, localizationMessage);
			message = getInitiatedMsg(owner, messageTemplate);
			break;
			
		case PTConstants.ACTION_STATUS_APPROVED:
			messageTemplate = getMessageTemplate(PTConstants.NOTIFICATION_OT_APPROVED, localizationMessage);
			message = getInitiatedMsg(owner, messageTemplate);
			break;
		}
		return message;
	}

	private String getInitiatedMsg(Owner owner, String message) {
		message = message.replace("<2>", owner.getOwnerDetails().getName());
		message = message.replace("<3>", owner.getOwnerDetails().getApplicationNumber());
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

}