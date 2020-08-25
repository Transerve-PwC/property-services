package org.egov.ps.test.validator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Owner;
import org.egov.ps.service.WorkflowCreationService;
import org.egov.ps.validator.PropertyValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PropertyValidatorServiceTest {
	
	@Autowired
	private PropertyValidator propertyValidator;
	
	@Test
	public void testvalidateDocumentsOnType() {
		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		
		String json = getFileContents("property_master_create_validate_document_owner.json");
		Owner owner = new Gson().fromJson(json, Owner.class);
		Map<String, String> errorMap = new HashMap<String,String>();
		
		propertyValidator.validateDocumentsOnType(requestInfo, "ch", owner, errorMap, "");
		
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
