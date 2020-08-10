package org.egov.ps.test.demands;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.ps.model.RentCollection;
import org.egov.ps.model.RentDemand;
import org.egov.ps.model.RentPayment;
import org.egov.ps.service.RentCollectionService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DemandServiceTests {

	ArrayList<RentDemand> initialDemands;

	@Autowired
	RentCollectionService rentCollectionService;
	
	@Before
	public void setup() throws ParseException {
		this.initialDemands = new ArrayList<RentDemand>(2);
		RentDemand demand1 = RentDemand.builder()
				.id("Demand1")
				.generationDate(getDateFromString("06/01/2020"))
				.collectionPrincipal(250.0)
				.build();
		RentDemand demand2 = RentDemand.builder()
				.id("Demand2")
				.generationDate(getDateFromString("07/01/2020"))
				.collectionPrincipal(250.0)
				.build();
		RentDemand demand3 = RentDemand.builder()
				.id("Demand3")
				.generationDate(getDateFromString("08/01/2020"))
				.collectionPrincipal(250.0)
				.build();
		this.initialDemands.add(demand1);
		this.initialDemands.add(demand2);
		this.initialDemands.add(demand3);
	}
	
	public void testSimpleDemand() throws ParseException {
		RentPayment payment1 = RentPayment.builder()
				.amountPaid(750.0)
				.dateOfPayment(getDateFromString("08/05/2020"))
				.receiptNo("Receipt 1")
				.build();
		
		
		List<RentCollection> collections = this.rentCollectionService.getCollectionsForPayment(this.initialDemands, payment1);
		
		assertEquals(collections.get(0).getInterestCollected(), 10.85, 0.01);
		assertEquals(collections.get(0).getPrincipalCollected(), 250, 0.01);
		assertEquals(collections.get(1).getInterestCollected(), 5.92, 0.01);
		assertEquals(collections.get(1).getPrincipalCollected(), 250, 0.01);
		assertEquals(collections.get(2).getInterestCollected(), 0, 0.01);
		assertEquals(collections.get(2).getPrincipalCollected(), 250 - 10.85 - 5.92, 0.01);
	}
	
	private static final String DATE_FORMAT = "mm/dd/yyyy";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT); 
	private Date getDateFromString(String date) throws ParseException {
		return dateFormatter.parse(date);
	}
}
