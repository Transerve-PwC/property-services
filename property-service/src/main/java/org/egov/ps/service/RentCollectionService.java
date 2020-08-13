package org.egov.ps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.model.RentCollection;
import org.egov.ps.model.RentDemand;
import org.egov.ps.model.RentPayment;
import org.egov.ps.model.User;
import org.springframework.stereotype.Service;

@Service
public class RentCollectionService {

	public List<RentCollection> getCollectionsForPayment(List<RentDemand> demands, RentPayment payment,
			User user) {
		double interestRate = 24;
		Date paymentDate = payment.getDateOfPayment();
		Double paybleAmount = payment.getAmountPaid();
		ArrayList<RentCollection> collections = new ArrayList<RentCollection>();
		Map<String, RentCollection> mapColletedInterest = new HashMap<String, RentCollection>();
		for (RentDemand rentDemand : demands) {
			RentCollection rentalCollection = new RentCollection();
			// Start the interest calculation
			Date generationDate = rentDemand.getGenerationDate();

			float daysBetween = ((paymentDate.getTime() - generationDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
			// If days cross the gross period then the interest is applicable
			if (daysBetween > rentDemand.getInitialGracePeriod()) {
				double interest = ((rentDemand.getCollectionPrincipal() * interestRate) / 100) * (daysBetween / 365);

				if (interest < paybleAmount) {
					rentalCollection.setInterestCollected(interest);
					paybleAmount = paybleAmount - interest;

				} else {
					// interest amount is more then the amount which have paid by user
					// 1 . set of the amount from the payable amount
					// 2. check if user have any past balance i yes pay remaining amounts from the
					// user's balance
					// if(user.getRemainingAmount()>0){
					if (user.getRemainingAmount() > (interest - paybleAmount)) {
						rentalCollection.setInterestCollected(interest);
						user.setRemainingAmount(user.getRemainingAmount() - (interest - paybleAmount));
					} else {
						rentalCollection.setInterestCollected(paybleAmount + user.getRemainingAmount());
						interest = interest - (paybleAmount + user.getRemainingAmount());
						user.setRemainingAmount(0.0);
						// save the remaining interest
						rentDemand.setRemainingInterest(interest);

					}
					// }
					// rentalCollection.setInterestCollected(paybleAmount);
					paybleAmount = 0.0;
				}
			} else
				rentalCollection.setInterestCollected(0.0);

			mapColletedInterest.put(rentDemand.getId(), rentalCollection);
			// End the interest calculation
		}
		// start the calculation
		for (RentDemand rentDemand : demands) {
			RentCollection rentalCollection = mapColletedInterest.get(rentDemand.getId());

			if (rentDemand.getCollectionPrincipal() < paybleAmount) {
				rentalCollection.setPrincipalCollected(rentDemand.getCollectionPrincipal());
				paybleAmount = paybleAmount - rentDemand.getCollectionPrincipal();
			} else {

				// CollectionPrincipal amount is more then the amount which have paid by user
				// 1 . set of the amount from the payable amount
				// 2. check if user have any past balance i yes pay remaining amounts from the
				// user's balance
				// if(user.getRemainingAmount()>0){
				if (user.getRemainingAmount() > (rentDemand.getCollectionPrincipal() - paybleAmount)) {
					rentalCollection.setPrincipalCollected(rentDemand.getCollectionPrincipal());
					user.setRemainingAmount(
							user.getRemainingAmount() - (rentDemand.getCollectionPrincipal() - paybleAmount));
				} else {
					rentalCollection.setPrincipalCollected(paybleAmount + user.getRemainingAmount());

					user.setRemainingAmount(0.0);
					// save the remaining amount
					rentDemand.setRemainingPrincipal(
							rentDemand.getCollectionPrincipal() - rentalCollection.getPrincipalCollected());

				}
				// }

				paybleAmount = 0.0;
			}
			collections.add(rentalCollection);

		}

		return collections;

	}
}
