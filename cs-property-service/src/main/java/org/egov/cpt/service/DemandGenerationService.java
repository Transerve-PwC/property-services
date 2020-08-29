package org.egov.cpt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.cpt.models.DemandCriteria;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemandGenerationService {

	private PropertyRepository propertyRepository;

	@Autowired
	public DemandGenerationService(PropertyRepository propertyRepository) {
		this.propertyRepository = propertyRepository;
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
				List<String> monthList = rentDemandList.stream().map(r -> r.getGenerationDate())
						.map(date -> new Date(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
						//.filter(d -> d.getYear()< currentYear)
						.map(local -> local.getMonth().toString()).collect(Collectors.toList());

				// System.out.println(monthList);
				if (demandCriteria.getMonth().isEmpty()) {
					String currentMonth = LocalDate.now().getMonth().toString();
					
					
					if (!monthList.contains(currentMonth.toUpperCase())) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear);
					} else {
						getYear(rentDemandList, currentMonth, property, collectionDemand.get(), currentYear);
								
						/*if(!year.contains(currentYear)) {
							// generate demand
							generateRentDemand(property, collectionDemand.get(), currentYear);
						}*/
					}
				} else {
					if (!monthList.contains(demandCriteria.getMonth().toUpperCase())) {
						// generate demand
						generateRentDemand(property, collectionDemand.get(), currentYear);
					} else {
						getYear(rentDemandList, demandCriteria.getMonth().toUpperCase(), property, collectionDemand.get(), currentYear);
								
						/*
						 * if(!year.contains(currentYear)) { // generate demand
						 * generateRentDemand(property, collectionDemand.get(), currentYear); }
						 */
					}
				}

			} else {
				// generate demand
				// generateRentDemand(property, collectionDemand);
			}
		});

	}

	private void getYear(List<RentDemand> rentDemandList, String month, Property property, RentDemand collectionRentDemand, int currentYear) {
		List<Integer> year = rentDemandList.stream().map(r -> r.getGenerationDate())
				.map(date -> new Date(date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
				.filter(d -> d.getMonth().toString().equals(month)).map(m -> m.getYear()).collect(Collectors.toList());
		if(!year.contains(currentYear)) {
			// generate demand
			generateRentDemand(property, collectionRentDemand, currentYear);
		}
	}

	private void generateRentDemand(Property property, RentDemand collectionDemand, int currentYear) {

		int oldYear = new Date(collectionDemand.getGenerationDate()).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate().getYear();

		RentDemand rentDemand = new RentDemand();
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
		rentDemand.setTenantId(property.getTenantId());
		// rentDemand.setAuditDetails();
	}

}
