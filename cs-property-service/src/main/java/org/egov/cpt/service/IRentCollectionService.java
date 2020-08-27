package org.egov.cpt.service;

import java.util.List;

import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.models.RentSummary;

public interface IRentCollectionService {

	/**
	 * Get the list of collections for the given demand and payments for the same
	 * property.
	 * 
	 * @apiNote When a new set of demands are saved in the database on every
	 *          _update.
	 * @apiNote This might change demand objects. This will create new Collection
	 *          objects.
	 * @param demands
	 * @param payment
	 * @return List<RentCollection> Collections to be saved in the database.
	 */
	public List<RentCollection> settle(List<RentDemand> demandsToBeSettled, List<RentPayment> paymentsToBeSettled,
			RentAccount account, double interestRate);

	/**
	 * Get the current rent summary by calculating from the given demands and
	 * collections for the same property.
	 * 
	 * @apiNote This is called every time we return a property in search.
	 * @apiNote This will not change the database in anyway.
	 * @param demands
	 * @param collections
	 * @param payment
	 * @return
	 */

	// public RentSummary getRentSummary(ArrayList<RentDemand> demands,
	// List<RentCollection> collections, List<RentPayment> payment);

	// Function parameters changed by Pooja
	public RentSummary paymentSummary(List<RentDemand> demands, RentAccount rentAccount);
}
