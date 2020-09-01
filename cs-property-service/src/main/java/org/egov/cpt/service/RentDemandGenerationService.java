package org.egov.cpt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemandCriteria;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RentDemandGenerationService {

	private PropertyRepository propertyRepository;
	
	private Producer producer;
	
	private PropertyConfiguration config;
	
	private RentCollectionService rentCollectionService;

	PropertyUtil propertyutil;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy");

	@Autowired
	public RentDemandGenerationService(PropertyRepository propertyRepository, Producer producer, PropertyConfiguration config,
			RentCollectionService rentCollectionService, PropertyUtil propertyutil) {
		this.propertyRepository = propertyRepository;
		this.producer = producer;
		this.config = config;
		this.rentCollectionService = rentCollectionService;
		this.propertyutil = propertyutil;
	}

	public void createDemand(RentDemandCriteria demandCriteria) {
		
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		propertyCriteria.setRelations(new ArrayList<>());
		List<Property> propertyList = propertyRepository.getProperties(propertyCriteria);

		propertyList.forEach(property -> {
			try {
			propertyCriteria.setPropertyId(property.getId());
			
			List<RentDemand> rentDemandList = propertyRepository.getPropertyRentDemandDetails(propertyCriteria);
			List<RentPayment> rentPaymentList = propertyRepository.getPropertyRentPaymentDetails(propertyCriteria);
			RentAccount rentAccount = propertyRepository.getPropertyRentAccountDetails(propertyCriteria);
			
			if (!rentDemandList.isEmpty()) {

				Comparator<RentDemand> compare = Comparator.comparing(RentDemand::getGenerationDate);
				Optional<RentDemand> collectionDemand = rentDemandList.stream().min(compare);

				int currentYear = LocalDate.now().getYear();
				List<String> dateList = rentDemandList.stream().map(r -> r.getGenerationDate())
						.map(date -> new Date(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString())
						.collect(Collectors.toList());

				if (demandCriteria.getDate().isEmpty()) {
					String currentDate = LocalDate.now().toString();

					if (!dateList.contains(currentDate)) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear, rentDemandList, rentPaymentList, rentAccount);
					}
				} else {
					String date = LocalDate.parse(demandCriteria.getDate(), FORMATTER).toString();
					if (!dateList.contains(date)) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear, rentDemandList, rentPaymentList, rentAccount);
						
					}
				}
			} else {
				log.debug("We are skipping generating rent demands for this property id: "+ property.getId() + " as there is no rent history");
			}
			} catch(Exception e) {
				log.error("exception occured for property id: " + property.getId());
			}
		}
				);

	}

	private void generateRentDemand(Property property, RentDemand collectionDemand, int currentYear,
			List<RentDemand> rentDemandList, List<RentPayment> rentPaymentList, RentAccount rentAccount) {

		int oldYear = new Date(collectionDemand.getGenerationDate()).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate().getYear();

		RentDemand rentDemand = new RentDemand();
		String rendDemandId = UUID.randomUUID().toString();
		rentDemand.setId(rendDemandId);
		rentDemand.setPropertyId(property.getId());

		rentDemand.setGenerationDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		Double collectionPrincipal = collectionDemand.getCollectionPrincipal();
		while (oldYear < currentYear) {
			collectionPrincipal = (collectionPrincipal
					* (100 + property.getPropertyDetails().getRentIncrementPercentage())) / 100;
			oldYear = oldYear + property.getPropertyDetails().getRentIncrementPeriod();
		}

		rentDemand.setCollectionPrincipal(collectionPrincipal);
		rentDemand.setRemainingPrincipal(rentDemand.getCollectionPrincipal());
		rentDemand.setInterestSince(rentDemand.getGenerationDate());
		// rentDemand.setTenantId(property.getTenantId());
		 //rentDemand.setAuditDetails(property.getAuditDetails());
		rentDemandList.add(rentDemand);
		// property.setDemands(Collections.singletonList(rentDemand));

		log.info("rend demand id: " + rendDemandId);
		log.info("collection principal: " + collectionPrincipal);
		property.setDemands(rentDemandList);
		property.setRentAccount(rentAccount);
		property.setPayments(rentPaymentList);
		
		if (rentPaymentList != null && rentAccount != null) {

			property.setRentCollections(rentCollectionService.settle(property.getDemands(), property.getPayments(),
					property.getRentAccount(), property.getPropertyDetails().getInterestRate()));
		}
		PropertyRequest propertyRequest = new PropertyRequest();
		propertyRequest.setProperties(Collections.singletonList(property));
		//RequestInfo requestInfo = propertyRequest.getRequestInfo();
		
		if (!CollectionUtils.isEmpty(property.getRentCollections())) {
			property.getRentCollections().forEach(collection -> {
				if (collection.getId() == null) {
					//AuditDetails rentAuditDetails = propertyutil
						//	.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
					collection.setId(UUID.randomUUID().toString());
					//collection.setAuditDetails(rentAuditDetails);
				}

			});
		}
		
		
		producer.push(config.getUpdatePropertyTopic(), propertyRequest);
	}

}
