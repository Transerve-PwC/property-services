package org.egov.cpt.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.ModeEnum;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyDetails;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemandCriteria;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PropertyUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class RentDemandGenerationServiceTest {

	@InjectMocks
	RentDemandGenerationService rentDemandGenerationService;

	@Mock
	private PropertyRepository propertyRepository;

	@Mock
	private Producer producer;

	@Mock
	private PropertyConfiguration config;

	@Mock
	private RentCollectionService rentCollectionService;

	@Mock
	PropertyUtil propertyutil;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(propertyRepository.getProperties(Mockito.any())).thenReturn(buildPropertyList());
	}

	@Test
	public void createDemandTest() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithDemandCriteriaAndDemandAlreadyExists() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteriaWhereDemandAlreadyExists(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithoutDemandHistory() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithoutDemandCriteria() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(new RentDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithoutDemandCriteriaAndDenandAlreadyExists() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any()))
				.thenReturn(buildRentDemandListWithCurrentDate());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(new RentDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestThrowsException() {
		Mockito.when(propertyRepository.getProperties(Mockito.any()))
				.thenReturn(buildPropertyListWithoutPropertyDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithoutRentAccount() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithoutPayments() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithRentCollectionsWithoutId() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(rentCollectionService.settle(Mockito.anyListOf(RentDemand.class),
				Mockito.anyListOf(RentPayment.class), Mockito.any(RentAccount.class), Mockito.anyDouble()))
				.thenReturn(buildRentCollections());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	@Test
	public void createDemandTestWithRentCollectionsWithId() {
		Mockito.when(propertyutil.getAuditDetails(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(buildAuditDetails());
		Mockito.when(rentCollectionService.settle(Mockito.anyListOf(RentDemand.class),
				Mockito.anyListOf(RentPayment.class), Mockito.any(RentAccount.class), Mockito.anyDouble()))
				.thenReturn(buildRentCollectionsWithId());
		Mockito.when(propertyRepository.getPropertyRentDemandDetails(Mockito.any())).thenReturn(buildRentDemandList());
		Mockito.when(propertyRepository.getPropertyRentPaymentDetails(Mockito.any()))
				.thenReturn(buildRentPaymentList());
		Mockito.when(propertyRepository.getPropertyRentAccountDetails(Mockito.any())).thenReturn(buildRentAccount());
		rentDemandGenerationService.createDemand(buildDemandCriteria(), buildRequestInfo());
	}

	private AuditDetails buildAuditDetails() {
		AuditDetails auditDetails = new AuditDetails();
		auditDetails.setCreatedBy("2743bf22-6499-4029-bd26-79e5d0ce6427");
		auditDetails.setCreatedTime(1599198579675L);
		auditDetails.setLastModifiedBy("2743bf22-6499-4029-bd26-79e5d0ce6427");
		auditDetails.setLastModifiedTime(1599198579675L);
		return auditDetails;
	}

	private RentDemandCriteria buildDemandCriteria() {
		RentDemandCriteria rentDemandCriteria = new RentDemandCriteria();
		rentDemandCriteria.setDate("01/04/2020");
		return rentDemandCriteria;
	}

	private RentDemandCriteria buildDemandCriteriaWhereDemandAlreadyExists() {
		RentDemandCriteria rentDemandCriteria = new RentDemandCriteria();
		rentDemandCriteria.setDate("02/06/2015");
		return rentDemandCriteria;
	}

	private RequestInfo buildRequestInfo() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setApiId("Rainmaker");
		requestInfo.setAuthToken("86fb987a-9a56-4ed7-9b96-bcc516a34352");
		requestInfo.setVer(".01");
		requestInfo.setMsgId("20170310130900|en_IN");
		User userInfo = new User();
		userInfo.setUuid("95fb987a-9a56-4ed7-9b96-bcc516a34352");
		requestInfo.setUserInfo(userInfo);
		return requestInfo;
	}

	private List<Property> buildPropertyList() {
		Property property = new Property();
		property.setId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		property.setTransitNumber("1234");
		property.setTenantId("ch.chandigarh");
		property.setArea("pato plaza");
		PropertyDetails propertyDetails = new PropertyDetails();
		propertyDetails.setInterestRate(24D);
		propertyDetails.setRentIncrementPercentage(5D);
		propertyDetails.setRentIncrementPeriod(1);
		property.setPropertyDetails(propertyDetails);
		return Collections.singletonList(property);
	}

	private List<Property> buildPropertyListWithoutPropertyDetails() {
		Property property = new Property();
		property.setId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		property.setTransitNumber("1234");
		property.setTenantId("ch.chandigarh");
		property.setArea("pato plaza");
		return Collections.singletonList(property);
	}

	private List<RentDemand> buildRentDemandList() {
		RentDemand rentDemand = new RentDemand();
		rentDemand.setCollectionPrincipal(100D);
		rentDemand.setGenerationDate(1433184076000L);
		rentDemand.setId("d2fed7b6-eb32-4b56-99d5-0361285e43eg");
		rentDemand.setInitialGracePeriod(1);
		rentDemand.setInterestSince(1433184076000L);
		rentDemand.setMode(ModeEnum.GENERATED);
		rentDemand.setPropertyId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		rentDemand.setRemainingPrincipal(100D);
		return Collections.singletonList(rentDemand);
	}

	private List<RentDemand> buildRentDemandListWithCurrentDate() {
		RentDemand rentDemand = new RentDemand();
		rentDemand.setCollectionPrincipal(100D);
		rentDemand.setGenerationDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		rentDemand.setId("d2fed7b6-eb32-4b56-99d5-0361285e43eg");
		rentDemand.setInitialGracePeriod(1);
		rentDemand.setInterestSince(1433184076000L);
		rentDemand.setMode(ModeEnum.GENERATED);
		rentDemand.setPropertyId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		rentDemand.setRemainingPrincipal(100D);
		return Collections.singletonList(rentDemand);
	}

	private List<RentPayment> buildRentPaymentList() {
		RentPayment rentPayment = new RentPayment();
		rentPayment.setId("d4fed7b6-eb22-4b56-99e7-0864295w42tf");
		rentPayment.setPropertyId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		rentPayment.setReceiptNo("rpt19");
		rentPayment.setAmountPaid(50D);
		rentPayment.setDateOfPayment(1599046968891L);
		return Collections.singletonList(rentPayment);
	}

	private RentAccount buildRentAccount() {
		RentAccount rentAccount = new RentAccount();
		rentAccount.setId("d6frd7b4-eb25-4b59-11d4-0361285e42df");
		rentAccount.setPropertyId("d1fed7b6-eb22-4b56-99d4-0361285e42df");
		rentAccount.setRemainingAmount(50D);
		return rentAccount;
	}

	private List<RentCollection> buildRentCollections() {
		RentCollection rentCollection = new RentCollection();
		rentCollection.setDemandId("d2fed7b6-eb32-4b56-99d5-0361285e43eg");
		rentCollection.setInterestCollected(50D);
		rentCollection.setPrincipalCollected(50D);
		rentCollection.setCollectedAt(1599046968891L);
		return Collections.singletonList(rentCollection);
	}

	private List<RentCollection> buildRentCollectionsWithId() {
		RentCollection rentCollection = new RentCollection();
		rentCollection.setId("w2fed7b6-eb32-4b56-99d5-0361285e43ut");
		rentCollection.setDemandId("d2fed7b6-eb32-4b56-99d5-0361285e43eg");
		rentCollection.setInterestCollected(50D);
		rentCollection.setPrincipalCollected(50D);
		rentCollection.setCollectedAt(1599046968891L);
		return Collections.singletonList(rentCollection);
	}
}
