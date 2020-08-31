package org.egov.ps.service;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Application;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EnrichmentServiceTest {

	@Autowired
	private EnrichmentService enrichmentService ;
	
	@Mock
	PropertyRepository propertyRepository;
	
	@Before
	public void init() {
	    MockitoAnnotations.initMocks(this);
	}

	@Test
	public void postEnrichMortgageDetails() {

		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		//String requestInfoJson =  "\"RequestInfo\": { \"apiId\": \"Rainmaker\", \"ver\": \".01\", \"ts\": \"\", \"action\": \"_create\", \"did\": \"1\", \"key\": \"\", \"msgId\": \"20170310130900|en_IN\", \"authToken\": \"4a959094-5d43-4303-ba5c-3c19fdee0f48\", \"UserInfo\": { \"id\": 1, \"uuid\": \"2743bf22-6499-4029-bd26-79e5d0ce6430\", \"userName\": \"ds\", \"name\": \"ds\", \"mobileNumber\": null, \"emailId\": null, \"locale\": null, \"type\": \"EMPLOYEE\", \"roles\": [ { \"name\": \"TL Approver\", \"code\": \"TL_APPROVER\", \"tenantId\": \"ch.chandigarh\" }, { \"name\": null, \"code\": \"CPS_DS\", \"tenantId\": \"ch.chandigarh\" }, { \"name\": \"Employee\", \"code\": \"EMPLOYEE\", \"tenantId\": \"ch.chandigarh\" } ], \"active\": true, \"tenantId\": \"ch.chandigarh\" } }";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		try {

			String json = getFileContents("MortgageDetails_property.json");
			Type listType = new TypeToken<List<org.egov.ps.model.Property>>() {}.getType();
			List<Property> properties =  new Gson().fromJson(json, listType);
			
			PropertyCriteria propertySearchCriteria = PropertyCriteria.builder()
					.propertyId("5cfa5df5-3fbb-4ace-a371-5f4d74d42e62").build();
			
			Mockito.when(
					propertyRepository.getProperties(propertySearchCriteria))
					.thenReturn(properties);
			
			String json_ = getFileContents("MortgageDetails_application.json");
			Type listType_ = new TypeToken<List<org.egov.ps.model.Application>>() {}.getType();
			List<Application> applications =  new Gson().fromJson(json_, listType_);
			
			
			ApplicationRequest request = ApplicationRequest.builder().requestInfo(requestInfo).applications(applications).build();
			
			enrichmentService.postEnrichMortgageDetails(request);

		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static String getFileContents(String fileName) {
		try {
			return IOUtils.toString(
					WorkflowCreationService.class.getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}