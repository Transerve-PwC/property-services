package org.egov.ps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.ps.model.RentCollection;
import org.egov.ps.model.RentDemand;
import org.egov.ps.model.RentPayment;
import org.springframework.stereotype.Service;

@Service
public class RentCollectionService {

	public List<RentCollection> getCollectionsForPayment(ArrayList<RentDemand> demands, RentPayment payment) {
		double interestRate = 24;
		Date paymentDate = payment.getDateOfPayment();
		Double paybleAmount = payment.getAmountPaid();
		ArrayList<RentCollection> collections = new ArrayList<RentCollection>();
		
		for (RentDemand rentDemand: demands) {
			RentCollection rentalCollection = new RentCollection();			
			Date generationDate = rentDemand.getGenerationDate();

			float daysBetween = ((paymentDate.getTime() - generationDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			if (daysBetween > rentDemand.getInitialGracePeriod()) {
				double interest = ((rentDemand.getCollectionPrincipal() * interestRate) / 100) * (daysBetween / 365);
				
				if (interest < paybleAmount) {
					rentalCollection.setInterestCollected(interest);
					paybleAmount = paybleAmount - interest;

				} else {
					rentalCollection.setInterestCollected(paybleAmount);
					paybleAmount = 0.0;
				}
			} else
				rentalCollection.setInterestCollected(0.0);

			if (rentDemand.getCollectionPrincipal() < paybleAmount) {
				rentalCollection.setPrincipalCollected(rentDemand.getCollectionPrincipal());
				paybleAmount = paybleAmount - rentDemand.getCollectionPrincipal();
			} else {
				rentalCollection.setPrincipalCollected(paybleAmount);
				paybleAmount = 0.0;
			}
			collections.add(rentalCollection);
		}
		return collections;

	}
}
