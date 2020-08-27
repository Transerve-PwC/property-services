package org.egov.ps.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.web.contracts.PropertyRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EnrichmentServiceTest {
	
	@Autowired
	private EnrichmentService enrichmentService;
	
	@Test
	public void testEnrichMortgageDetailsRequest() {
		

		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		String json = getFileContents("MortgageDetails_property.json");
		Type listType = new TypeToken<List<org.egov.ps.model.Property>>() {}.getType();
		List<org.egov.ps.model.Property> propertyList = new Gson().fromJson(json, listType);
		
		PropertyRequest propertyRequest = PropertyRequest.builder().requestInfo(requestInfo).properties(propertyList).build();
		enrichmentService.enrichMortgageDetailsRequest(propertyRequest);
		
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