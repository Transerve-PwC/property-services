package org.egov.cpt.consumer;

import java.util.HashMap;

import org.egov.cpt.service.OwnershipTransferService;
import org.egov.cpt.service.PropertyService;
import org.egov.cpt.service.notification.PropertyNotificationService;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyConsumer {

	private PropertyNotificationService notificationService;
	
	private PropertyService propertyService;
	
	private OwnershipTransferService ownershipTransferService;
	
	@Autowired
	public PropertyConsumer(PropertyNotificationService notificationService, PropertyService propertyService, OwnershipTransferService ownershipTransferService) {
		this.notificationService = notificationService;
		this.propertyService = propertyService;
		this.ownershipTransferService = ownershipTransferService;
	}
	
	@KafkaListener(topics = {"${persister.save.property.topic}", "{$persister.update.property.topic}"})
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		OwnershipTransferRequest ownershipTransferRequest = new OwnershipTransferRequest();
		try {
			log.info("Consuming record: " + record);
			ownershipTransferRequest = mapper.convertValue(record, OwnershipTransferRequest.class);
		} catch (Exception e) {
			log.error("Error while listening to value: " + record + " on topic " + topic + ": " + e);
		}
		notificationService.process(ownershipTransferRequest);
	}
}
