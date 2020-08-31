package org.egov.cpt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DemandCriteria;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DemandGenerationService {

	private PropertyRepository propertyRepository;
	
	private Producer producer;
	
	private PropertyConfiguration config;
	
	private RentCollectionService rentCollectionService;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy");
	@Autowired
	public DemandGenerationService(PropertyRepository propertyRepository, Producer producer, PropertyConfiguration config) {
		this.propertyRepository = propertyRepository;
		this.producer = producer;
		this.config = config;
	}

	public void createDemand(DemandCriteria demandCriteria) {
		
		PropertyCriteria propertyCriteria = new PropertyCriteria();
		List<Property> propertyList = propertyRepository.getProperties(propertyCriteria);
		// System.out.println(propertyList);

		propertyList.forEach(property -> {
			propertyCriteria.setPropertyId(property.getId());
			List<RentDemand> rentDemandList = propertyRepository.getPropertyRentDemandDetails(propertyCriteria);
			// System.out.println(rentDemandList);

			if (!rentDemandList.isEmpty()) {

				Comparator<RentDemand> compare = Comparator.comparing(RentDemand::getGenerationDate);
				Optional<RentDemand> collectionDemand = rentDemandList.stream().min(compare);

				int currentYear = LocalDate.now().getYear();
				List<String> dateList = rentDemandList.stream().map(r -> r.getGenerationDate())
						.map(date -> new Date(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString())
						// .filter(d -> d.getYear()< currentYear)
						// .map(local -> local.getMonth().toString())
						.collect(Collectors.toList());

				// System.out.println(monthList);
				if (demandCriteria.getDate().isEmpty()) {
					String currentDate = LocalDate.now().toString();

					if (!dateList.contains(currentDate)) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear);
					}
				} else {
					String date = LocalDate.parse(demandCriteria.getDate(), FORMATTER).toString();
					if (!dateList.contains(date)) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear);
					}
				}
			} else {
				// generate demand
				// generateRentDemand(property, collectionDemand);
			}
		});

	}

	private void generateRentDemand(Property property, RentDemand collectionDemand, int currentYear) {

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
		//rentDemand.setTenantId(property.getTenantId());
		// rentDemand.setAuditDetails();
		
		//property.setDemands(Collections.singletonList(rentDemand));
		
		log.info("rend demand id: "+ rendDemandId);
		log.info("collection principal: "+ collectionPrincipal);
		
		if(property.getPayments() != null && property.getRentAccount() != null) {
			rentCollectionService.settle(property.getDemands(), property.getPayments(), property.getRentAccount(),
					property.getPropertyDetails().getInterestRate());
		}
		
		//producer.push(config.getSaveRentDemand(), rentDemand);
	}

}
