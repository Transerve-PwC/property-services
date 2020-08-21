package org.egov.ps.service;

import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WorkflowCreationServiceTest {

	@Autowired
	Util util;

	@Autowired
	WorkflowCreationService workflowCreationService;


	@Test
	public void createWorkflows() throws Exception {
		//1 create object RequestInfo
		RequestInfo requestInfo = RequestInfo.builder()
									.apiId("Rainmaker")
									.ver("01")
									.key("")
									.msgId("20170310130900|en_IN")
									.authToken("833b0a57-bbc5-4194-a961-bdb3794fa284")
									.action("_create")
									.ts(null)
									.build();
		
		 
		workflowCreationService.createWorkflows(requestInfo);
		
		//doNothing().when(workflowCreationService).createWorkflows(requestInfo);
		
		assertNull(null, null);
		
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