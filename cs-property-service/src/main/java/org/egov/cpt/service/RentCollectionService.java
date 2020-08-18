package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;

import org.springframework.stereotype.Service;

@Service
public class RentCollectionService {
        	List<RentCollection> collections = new ArrayList<RentCollection>();
	Set<RentDemand> processedDemand=new LinkedHashSet<RentDemand>();
	
/**
 * This method is process the demand against given payment 
 * Save the collected amount 
 * return the paid demands  
 * @param demands
 * @param payment
 * @param rentAccount
 * @return List<RentDemand>
 */
public List<RentDemand> getCollectionsForPayment(List<RentDemand> demands, RentPayment payment,
			RentAccount rentAccount) {
		List<RentDemand> paidDemand=new ArrayList<RentDemand>();
		double interestRate = 24;
		Double paidAmount = payment.getAmountPaid();
		
		Map<String, RentCollection> mapColletedInterest = new HashMap<String, RentCollection>();
		for (RentDemand rentDemand : demands) {
			RentCollection rentalCollection = new RentCollection();
			// Start the interest calculation
			//Date generationDate = new Date(rentDemand.getGenerationDate());

			float daysBetween = ((payment.getDateOfPayment() - rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24)) + 1;
			// If days cross the gross period then the interest is applicable
			if (daysBetween > rentDemand.getInitialGracePeriod()) {
				double interest = ((rentDemand.getRemainingPrincipal()* interestRate) / 100) * (daysBetween / 365);

				if (interest < paidAmount) {
					rentalCollection.setInterestCollected(interest);
					paidAmount = paidAmount - interest;

				} else {
					// interest amount is more then the amount which have paid by user
					// 1 . set of the amount from the payable amount
					// 2. check if user have any past balance i yes pay remaining amounts from the
					// user's balance
					// if(user.getRemainingAmount()>0){
					
					if (rentAccount.getRemainingAmount() > (interest - paidAmount)) {
						rentalCollection.setInterestCollected(interest);
						rentAccount.setRemainingAmount((rentAccount.getRemainingAmount() - (interest - paidAmount)));
					} else {
						rentalCollection.setInterestCollected(paidAmount + rentAccount.getRemainingAmount());
						interest = interest - (paidAmount + rentAccount.getRemainingAmount());
						rentAccount.setRemainingAmount(0.0);
						// save the remaining interest
						//rentDemand.setRemainingInterest(interest);

					}
					// }
					// rentalCollection.setInterestCollected(paidAmount);
					paidAmount = 0.0;
				}
			} else
				rentalCollection.setInterestCollected(0.0);

			mapColletedInterest.put(rentDemand.getId(), rentalCollection);
			// End the interest calculation
		}
		// start the calculation
		for (RentDemand rentDemand : demands) {
			RentCollection rentalCollection = mapColletedInterest.get(rentDemand.getId());
                        rentalCollection.setDemandId(rentDemand.getId());
                        rentalCollection.setPaymentId(payment.getId());

			if (rentDemand.getRemainingPrincipal()< paidAmount) {
				rentalCollection.setPrincipalCollected(rentDemand.getRemainingPrincipal());
				paidAmount = paidAmount - rentDemand.getRemainingPrincipal();
                                rentDemand.setRemainingPrincipal(0.0);
                                
			} else {

				// CollectionPrincipal amount is more then the amount which have paid by user
				// 1 . set of the amount from the payable amount
				// 2. check if user have any past balance i yes pay remaining amounts from the
				// user's balance
				// if(user.getRemainingAmount()>0){
				if (rentAccount.getRemainingAmount() > (rentDemand.getRemainingPrincipal()- paidAmount)) {
					rentalCollection.setPrincipalCollected(rentDemand.getRemainingPrincipal());
					rentAccount.setRemainingAmount(
							rentAccount.getRemainingAmount() - (rentDemand.getRemainingPrincipal()- paidAmount));
                                        rentDemand.setRemainingPrincipal(0.0);
                                } else {
					rentalCollection.setPrincipalCollected(paidAmount + rentAccount.getRemainingAmount());

					rentAccount.setRemainingAmount(0.0);
					// save the remaining amount
					rentDemand.setRemainingPrincipal(
							rentDemand.getRemainingPrincipal()- rentalCollection.getPrincipalCollected());

				}
				// }

				paidAmount = 0.0;
			}
			if(rentDemand.getRemainingPrincipal()==0) {
				paidDemand.add(rentDemand);

                                
				
				
			}
			collections.add(rentalCollection);
                        rentDemand.setInterestSince(payment.getDateOfPayment());
                        processedDemand.add(rentDemand);    
		}
                if(paidAmount>0)
                    rentAccount.setRemainingAmount(rentAccount.getRemainingAmount()+paidAmount);
                
                
		return paidDemand;

	}

	
/**
 * This method accept the rent demands and payments and call the method to process the demand
 * @param demands
 * @param payments
 * @param rentAccount
 * @return List<RentDemand>
 */
public Map getCollectionsForPayment(List<RentDemand> demands, List<RentPayment> payments,
			RentAccount rentAccount) {
		Map<String,Collection> responseMap=new HashMap<String,Collection>();
		List<RentDemand> lstRentDemandProcess;
		for(RentPayment rentPayment:payments) {
			
			lstRentDemandProcess=new ArrayList<RentDemand>();
			Date paymentDate = new Date(rentPayment.getDateOfPayment());
			
			for(RentDemand rentDemand:demands) {
				Date demandDate = new Date(rentDemand.getGenerationDate());
                                //filter out the demands which have earlier date than payment 
				if(demandDate.compareTo(paymentDate)<0) {
					lstRentDemandProcess.add(rentDemand);
				}
			}
                        //call the function to proceed demand against payment 
			List<RentDemand> paidDemands=getCollectionsForPayment(lstRentDemandProcess,rentPayment,rentAccount);
		    demands.removeAll(paidDemands);
                        
		}
                // cron job
                if(demands.size()>0 && rentAccount.getRemainingAmount()>0){
                    RentPayment payment1 = new RentPayment();
                        payment1.setId("payment");
                        payment1.setAmountPaid(rentAccount.getRemainingAmount());
                        payment1.setDateOfPayment(new Date().getTime());
                        payment1.setReceiptNo("Receipt" );
                        rentAccount.setRemainingAmount(0.0);
                        List<RentDemand> paidDemands=getCollectionsForPayment(demands,payment1,rentAccount);
			            demands.removeAll(paidDemands);
                        
                }
                responseMap.put("demand", processedDemand);
                responseMap.put("colection", collections);
	 return responseMap;	
	}
    


 public void paymentSummary(List<RentDemand> demands,RentAccount rentAccount) {
	 double balancePrincipal =0;
	 double balanceInterest =0;
	 double balanceAmount =0;
	 final double interestRate = 24;
	 
	 
	 for(RentDemand rentDemand:demands) {
		 double interest=0;
		 float daysBetween = ((new Date().getTime()- rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24)) + 1;
			// If days cross the gross period then the interest is applicable
			if (daysBetween > rentDemand.getInitialGracePeriod()) {
				 interest = ((rentDemand.getRemainingPrincipal()* interestRate) / 100) * (daysBetween / 365);
			}
			balanceInterest+=interest;
			balancePrincipal+=rentDemand.getRemainingPrincipal();	
		 
		 
	 }
	 balanceAmount=rentAccount.getRemainingAmount();
	 
	 
 }


}
