package org.egov.ps.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.ps.model.RentCollection;
import org.egov.ps.model.RentDemand;
import org.egov.ps.model.RentPayment;
import org.egov.ps.model.RentSummary;

public interface IRentCollectionService {

	/**
	 * Get the list of collections for the given demand and payments for the same property.
	 * 
	 * @apiNote When a new set of demands are saved in the database on every _update.
	 * @apiNote This might change demand objects. This will create new Collection objects.
	 * @param demands
	 * @param payment
	 * @return List<RentCollection> Collections to be saved in the database.
	 */
	public List<RentCollection> getCollectionsForPayment(ArrayList<RentDemand> demands, List<RentPayment> payment);
	
	/**
	 * Get the current rent summary by calculating from the given demands and collections for the same property.
	 * 
	 * @apiNote This is called every time we return a property in search.
	 * @apiNote This will not change the database in anyway.
	 * @param demands
	 * @param collections
	 * @param payment
	 * @return
	 */
	public RentSummary getRentSummary(ArrayList<RentDemand> demands, List<RentCollection> collections, List<RentPayment> payment);
	
	/**
	 * Process the incoming payment.
	 * 
	 * @apiNote This will generate new collections that will be saved and will also modify existing demand objects.
	 * @apiNote This will be called from PaymentKafkaConsumer
	 * @param demands
	 * @param collections
	 * @param payment
	 * @return List<RentCollection> Generated collection objects for the new payment.
	 */
	public List<RentCollection> processNewPayment(ArrayList<RentDemand> demands, RentPayment payment);
	
	
}
