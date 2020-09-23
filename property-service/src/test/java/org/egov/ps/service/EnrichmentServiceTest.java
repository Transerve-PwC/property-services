package org.egov.ps.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Application;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EnrichmentServiceTest {

	@Autowired
	EnrichmentService enrichmentService ;

	
	@Test
	public void postProcessNotifications() {
		try {
			//Step 1  prepare RequestInfo
			//String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
			
			String requestInfoJson = "{\"apiid\":\"rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgid\":\"20170310130900|en_in\",\"authtoken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userinfo\":{\"tenantid\":\"ch\",\"id\":8,\"username\":\"any\",\"mobilenumber\":\"8866581197\",\"emailid\":\"mineshbhadeshia@gmail.com\" }}";
			
			RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
			String json_ = getFileContents("notification_application.json");
			Type listType_ = new TypeToken<List<org.egov.ps.model.Application>>() {}.getType();
			List<Application> applications =  new Gson().fromJson(json_, listType_);

			ApplicationRequest request = ApplicationRequest.builder().requestInfo(requestInfo).applications(applications).build();
			enrichmentService.enrichProcessNotifications(request);
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