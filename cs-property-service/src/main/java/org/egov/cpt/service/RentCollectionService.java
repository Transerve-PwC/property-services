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
import org.egov.cpt.models.RentAccountStatement;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentCollection.CollectionAgainst;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemand.ModeEnum;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.models.RentSummary;
import org.springframework.stereotype.Service;

@Service
public class RentCollectionService implements IRentCollectionService{
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
	
private  List<RentDemand> getCollectionsForPayment(List<RentDemand> demands, RentPayment payment,RentAccount rentAccount) {
		List<RentDemand> paidDemand=new ArrayList<RentDemand>();
		double interestRate = 24;
		Double paidAmount = payment.getAmountPaid();
		
		Map<String, RentCollection> mapColletedInterest = new HashMap<String, RentCollection>();
		for (RentDemand rentDemand : demands) {
			if(rentDemand.getRemainingPrincipal()<=0)
				continue;
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
			if(rentDemand.getRemainingPrincipal()<=0)
				continue;
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
//rentalCollection.setCollectionAgainst(CollectionAgainst.ACCOUNTBALANCE);
			collections.add(rentalCollection);
                        rentDemand.setInterestSince(payment.getDateOfPayment());
                        processedDemand.add(rentDemand);    
		}
                if(paidAmount>0)
                    rentAccount.setRemainingAmount(rentAccount.getRemainingAmount()+paidAmount);
                
        payment.setProceed(true);         
		return paidDemand;

	}

	
/**
 * Get the list of collections for the given demand and payments for the same property.
 * 
 * @apiNote When a new set of demands are saved in the database on every _update.
 * @apiNote This might change demand objects. This will create new Collection objects.
 * @param demands
 * @param payment
 * @return List<RentCollection> Collections to be saved in the database.
 */
@Override
public List<RentCollection> settle(List<RentDemand> demandsToBeSettled, List<RentPayment> paymentsToBeSettled,RentAccount account) 
//public List<RentCollection> settle(List<RentDemand> demandsToBeSettled, List<RentPayment> paymentsToBeSettled, RentAccount account)
{
	//	Map<String,Collection> responseMap=new HashMap<String,Collection>();
		List<RentDemand> lstRentDemandProcess;
		for(RentPayment rentPayment:paymentsToBeSettled) {
			if(rentPayment.isProceed())
				continue;
			
			lstRentDemandProcess=new ArrayList<RentDemand>();
			Date paymentDate = new Date(rentPayment.getDateOfPayment());
			
			for(RentDemand rentDemand:demandsToBeSettled) {
				if(rentDemand.getRemainingPrincipal()<=0)
					continue;
				Date demandDate = new Date(rentDemand.getGenerationDate());
                                //filter out the demands which have earlier date than payment 
				if(demandDate.compareTo(paymentDate)<0) {
					lstRentDemandProcess.add(rentDemand);
				}
			}
                        //call the function to proceed demand against payment 
			List<RentDemand> paidDemands=getCollectionsForPayment(lstRentDemandProcess,rentPayment,account);
			
			demandsToBeSettled.removeAll(paidDemands);
                        
		}
                // cron job
                if(demandsToBeSettled.size()>0 && account.getRemainingAmount()>0){
                    RentPayment payment1 = new RentPayment();
                        //payment1.setId("payment");
                        payment1.setAmountPaid(account.getRemainingAmount());
                        payment1.setDateOfPayment(new Date().getTime());
                       // payment1.setReceiptNo("Receipt" );
                     //   payment1.setMode(org.egov.cpt.models.RentPayment.Mod);
                        account.setRemainingAmount(0.0);
                        List<RentDemand> paidDemands=getCollectionsForPayment(demandsToBeSettled,payment1,account);
                        demandsToBeSettled.removeAll(paidDemands);
                        
                }
                demandsToBeSettled.clear();
                demandsToBeSettled.addAll(processedDemand);
	 return collections;	
	}
    


/**
 * Get the current rent summary by calculating from the given demands and collections for the same property.
 * 
 * @apiNote This is called every time we return a property in search.
 * @apiNote This will not change the database in anyway.
 * @param demands
 * @param collections
 * @param payment
 * @return RentSummary
 */
@Override
 public RentSummary paymentSummary(List<RentDemand> demands,RentAccount rentAccount) {
	 double balancePrincipal =0;
	 double balanceInterest =0;
	 double balanceAmount =0;
	 final double interestRate = 24;
	 RentSummary rentSummary=new RentSummary();
	 
	 
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
	 rentSummary.setBalanceAmount(rentAccount.getRemainingAmount());
	 rentSummary.setBalanceInterest(balanceInterest);
	 rentSummary.setBalancePrincipal(balancePrincipal);
	 return rentSummary;
	 
	 
	 
 }
 
 
/**
 * @apiNote This will provide the account statement between the date specified by the user. 
 * @param demands
 * @param payments
 * @param lstCollection
 * @return List<RentAccountStatement> 
 */
	
	@Override
	public List<RentAccountStatement> accountStatement (List<RentDemand> demands, List<RentPayment> payments,List<RentCollection> lstCollection) {
		
		final double interestRate=24;
		//ArrayList lstProcessRentDemand;
		ArrayList<RentAccountStatement> lstAccountStatement=new ArrayList<RentAccountStatement>();
                double remainingPrincipal=0.0;
		double remainingInterest=0.0;
                double outstandingBalance=0.0;
		for(RentPayment rentPayment:payments) {
			
			
			//lstProcessRentDemand=new ArrayList<RentDemand>();
			Date paymentDate = new Date(rentPayment.getDateOfPayment());
			
			
			
			double collectedAmount=0.0;
                        ArrayList<RentDemand> lstDemandTobeProcess=new ArrayList<RentDemand>();
		
			for(RentDemand rentDemand:demands) {
				
				Date demandDate = new Date(rentDemand.getGenerationDate());
				
                                //filter out the demands which have earlier date than payment 
				if(demandDate.compareTo(paymentDate)<=0) {
                                    lstDemandTobeProcess.add(rentDemand);
					RentAccountStatement rentAccountStatement=new RentAccountStatement();
					rentAccountStatement.setDate(rentDemand.getGenerationDate());
					rentAccountStatement.setAmount(rentDemand.getCollectionPrincipal());
					rentAccountStatement.setType("D");
					rentAccountStatement.setRemainingPrincipal(remainingPrincipal+rentDemand.getCollectionPrincipal());
					
					
					
					remainingPrincipal=	remainingPrincipal+rentDemand.getCollectionPrincipal();
					float daysBetween = ((rentPayment.getDateOfPayment() - rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24)) + 1;
					// If days cross the gross period then the interest is applicable
					if (daysBetween >= rentDemand.getInitialGracePeriod()) {
						double interest = ((rentDemand.getCollectionPrincipal()* interestRate) / 100) * (daysBetween / 365);
						remainingInterest=remainingInterest+interest;
					}
					rentAccountStatement.setRemainingInterest(remainingInterest);
					rentAccountStatement.setDueAmount(rentAccountStatement.getRemainingInterest()+rentAccountStatement.getRemainingPrincipal());
                                        lstAccountStatement.add(rentAccountStatement);
						
			}
			                        
		}
			RentAccountStatement rentAccountStatement=new RentAccountStatement();
			rentAccountStatement.setDate(rentPayment.getDateOfPayment());
			rentAccountStatement.setAmount(rentPayment.getAmountPaid());
			rentAccountStatement.setType("C");
			rentAccountStatement.setRemainingPrincipal(0.0);
			rentAccountStatement.setRemainingInterest(0.0);
			rentAccountStatement.setDueAmount(0.0);
			
			
			for(RentCollection rentCollection:lstCollection) {
                            if(null != rentCollection.getPaymentId()){
				if(rentCollection.getPaymentId().equals(rentPayment.getId())) {
					collectedAmount=collectedAmount+rentCollection.getPrincipalCollected();
					
				}
                            }else{
                               
                                if(null != lstDemandTobeProcess.stream()
                                        .filter(tmpRentDemand->tmpRentDemand.getId().equals(rentCollection.getDemandId()))
                                        .findAny()
                                        .orElse(null))
                                           collectedAmount=collectedAmount+rentCollection.getPrincipalCollected();
                            }
			}
                        if((collectedAmount+remainingInterest)<rentPayment.getAmountPaid())
                            outstandingBalance=rentPayment.getAmountPaid()-collectedAmount;
			if(remainingInterest<=collectedAmount) {
				rentAccountStatement.setRemainingInterest(0.0);
                                remainingInterest=0;
				collectedAmount=collectedAmount-remainingInterest;
			}
			else {
				rentAccountStatement.setRemainingInterest(remainingInterest-collectedAmount);
                                remainingInterest=remainingInterest-collectedAmount;
				collectedAmount=0.0;
			}
			if(remainingPrincipal<=collectedAmount) {
				rentAccountStatement.setRemainingPrincipal(0.0-outstandingBalance);
                                remainingPrincipal=0;
				collectedAmount=collectedAmount-remainingPrincipal;
			}
			else {
				rentAccountStatement.setRemainingPrincipal(remainingPrincipal-collectedAmount-outstandingBalance);
                                remainingPrincipal=remainingPrincipal-collectedAmount;
				collectedAmount=0.0;
			}
                        rentAccountStatement.setDueAmount(rentAccountStatement.getRemainingInterest()+rentAccountStatement.getRemainingPrincipal());
                                        
		lstAccountStatement.add(rentAccountStatement);
                demands.removeAll(lstDemandTobeProcess);
	
	}
                if(demands.size()>0){
                    for(RentDemand rentDemand:demands){
                        RentAccountStatement rentAccountStatement=new RentAccountStatement();
					rentAccountStatement.setDate(rentDemand.getGenerationDate());
					rentAccountStatement.setAmount(rentDemand.getCollectionPrincipal());
					rentAccountStatement.setType("D");
					rentAccountStatement.setRemainingPrincipal(remainingPrincipal+rentDemand.getCollectionPrincipal()-outstandingBalance);
					
					
					
					remainingPrincipal=	remainingPrincipal+rentDemand.getCollectionPrincipal()-outstandingBalance;
					float daysBetween = ((new Date().getTime() - rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24)) + 1;
					// If days cross the gross period then the interest is applicable
					if (daysBetween >= rentDemand.getInitialGracePeriod()) {
						double interest = ((rentDemand.getCollectionPrincipal()* interestRate) / 100) * (daysBetween / 365);
						remainingInterest=remainingInterest+interest;
					}
					rentAccountStatement.setRemainingInterest(remainingInterest);
					rentAccountStatement.setDueAmount(rentAccountStatement.getRemainingInterest()+rentAccountStatement.getRemainingPrincipal());
                                        lstAccountStatement.add(rentAccountStatement);
                    }
                    
                }
                return lstAccountStatement;
		//System.out.println(lstAccountStatement);
	}
	
	


}
