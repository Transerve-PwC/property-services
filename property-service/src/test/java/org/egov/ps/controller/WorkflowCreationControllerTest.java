package org.egov.ps.controller;


import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.service.WorkflowCreationService;
import org.egov.ps.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WorkflowCreationControllerTest {
	@Autowired
	Util util;

	@Autowired
	WorkflowCreationController workflowCreationController;


	@Test
	public void createTest() throws Exception {
		String json = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(json, RequestInfo.class);
		assertNotNull(workflowCreationController.create(requestInfo));
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