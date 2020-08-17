package org.egov.cpt.service;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DemandServiceTests {

	ArrayList<RentDemand> initialDemands;
	ArrayList<RentPayment> initialPayments;
	RentAccount rentAccount;
	

	@Autowired
	RentCollectionService rentCollectionService;
	
	
	@Before
	public void setup() throws ParseException {
		rentAccount=new RentAccount();
        rentAccount.setId("user1");
        rentAccount.setPropertyId("prop101");
        rentAccount.setRemainingAmount(0.0);
		
		
	   // this.user.setRemainingAmount(0.0);
		
	    String[] dynamicDates = {"01 06 2020","01 07 2020","01 08 2020","01 09 2020","01 10 2020","01 11 2020","01 12 2020"};
		Double[] dynamicCollectionPrincipal = {250.0,250.0,250.0,250.0,250.0,250.0,250.0};
		String[] paymentDates = {"05 04 2020","05 05 2020"};
		Double[] paymentAmount = {750.0,900.0};
		
		this.initialDemands = new ArrayList<RentDemand>(dynamicDates.length);		
		initialPayments = new ArrayList<RentPayment>(paymentDates.length);

		for(int i=0; i < dynamicDates.length;i++) {
			RentDemand demand = RentDemand.builder()
					.id("Demand"+(i+1))
					.generationDate(getDateFromString(dynamicDates[i]).getTime())
					.collectionPrincipal(dynamicCollectionPrincipal[i])
					.build();
			this.initialDemands.add(demand);
		}
		
		 for(int i=0; i < paymentDates.length;i++) {
			 RentPayment payment=RentPayment.builder()
					 .id("payment"+i)
					 .amountPaid(paymentAmount[i])
					 .receiptNo("Receipt "+i)
					 .dateOfPayment(getDateFromString(paymentDates[i]).getTime())
					 .build();
				
				initialPayments.add(payment);
			}
	}
	
	@Test
	public void testSimpleDemand() throws ParseException {
		RentPayment payment1 = RentPayment.builder()
				.amountPaid(750.0)
				.dateOfPayment(getDateFromString("05 08 2020").getTime())
				.receiptNo("Receipt 1")
				.build();
		
		Map<String,List> collections = this.rentCollectionService.getCollectionsForPayment(this.initialDemands, this.initialPayments,this.rentAccount);
	//	List<RentCollection> secondCollections = this.rentCollectionService.getCollectionsForPayment(this.initialDemands.subList(3, 6), payment1,this.remainingAmount);
				
//		assertEquals(collections.get(0).getInterestCollected(), 10.85, 0.01);
//		assertEquals(collections.get(0).getPrincipalCollected(), 250, 0.01);
//		assertEquals(collections.get(1).getInterestCollected(), 5.92, 0.01);
//		assertEquals(collections.get(1).getPrincipalCollected(), 250, 0.01);
//		assertEquals(collections.get(2).getInterestCollected(), 0, 0.01);
//		assertEquals(collections.get(2).getPrincipalCollected(), 250 - 10.85 - 5.92, 0.01);
	}
	
	
	private static final String DATE_FORMAT = "dd MM yyyy";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT); 
	private Date getDateFromString(String date) throws ParseException {
		return dateFormatter.parse(date);
	}
}
