package org.egov.cpt.consumer;

import java.util.HashMap;

import org.egov.cpt.service.notification.PropertyNotificationService;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
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
	
	@Autowired
	public PropertyConsumer(PropertyNotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@KafkaListener(topics = {"${ownership.transfer.save.topic}", "${ownership.transfer.update.topic}"})
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			OwnershipTransferRequest ownershipTransferRequest = new OwnershipTransferRequest();
			log.info("Consuming record: " + record);
			ownershipTransferRequest = mapper.convertValue(record, OwnershipTransferRequest.class);
			notificationService.process(ownershipTransferRequest);
		} catch (Exception e) {
			log.error("Error while listening to value: " + record + " on topic " + topic + ": " + e);
		}
	}
}
