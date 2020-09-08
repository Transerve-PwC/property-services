package org.egov.cpt.controller;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.RentDemandCriteria;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.service.RentDemandGenerationService;
import org.egov.cpt.web.controllers.RentDemandGenerationController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RentDemandGenerationControllerTest {

	@InjectMocks
	RentDemandGenerationController rentDemandGenerationController;

	@Mock
	private RentDemandGenerationService demandGenerationService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createRequestTest() {
		rentDemandGenerationController.create(buildDemandCriteria(), buildRequestInfoWrapper());
	}

	private RentDemandCriteria buildDemandCriteria() {
		RentDemandCriteria rentDemandCriteria = new RentDemandCriteria();
		rentDemandCriteria.setDate("01/04/2020");
		return rentDemandCriteria;
	}

	private RequestInfoWrapper buildRequestInfoWrapper() {
		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setApiId("Rainmaker");
		requestInfo.setAuthToken("86fb987a-9a56-4ed7-9b96-bcc516a34352");
		requestInfo.setVer(".01");
		requestInfo.setMsgId("20170310130900|en_IN");
		requestInfoWrapper.setRequestInfo(requestInfo);
		return requestInfoWrapper;
	}
}
