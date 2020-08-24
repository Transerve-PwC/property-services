package org.egov.ps.service;

import static org.junit.Assert.assertNotNull;

import org.egov.common.contract.request.RequestInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MDMSServiceTest {
	@Autowired
	MDMSService mDMSService; 
	
	@Test
	public void getDocumentConfig() throws Exception {
		
		String json = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_search\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(json, RequestInfo.class);
		
		assertNotNull(mDMSService.getDocumentConfig("documents", requestInfo, "ch"));
	}
}
