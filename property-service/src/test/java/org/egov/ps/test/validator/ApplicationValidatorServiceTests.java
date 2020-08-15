package org.egov.ps.test.validator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.service.MDMSService;
import org.egov.ps.validator.ApplicationValidatorService;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApplicationValidatorServiceTests {

	@Autowired
	ApplicationValidatorService validatorService;

	@Autowired
	private MDMSService mdmsService;

	private String SampleApplicationType = "SampleType";

	@Test
	public void testSomething() throws JSONException {
		String configJSON = getFileContents("simpleApplicationConfig.json");
		String simpleApplicationObjectJSON = getFileContents("simpleApplicationObject.json");
		List<Map<String, Object>> result = JsonPath.read(configJSON, "$.fields");
		// System.out.println(result.getClass());
		// System.out.println(new JSONObject(json).toString());
		Mockito.when(
				mdmsService.getApplicationConfig(eq(SampleApplicationType), any(RequestInfo.class), any(String.class)))
				.thenReturn(result);
		this.validatorService.performValidationsFromMDMS(SampleApplicationType,
				JsonPath.read(simpleApplicationObjectJSON, "$"), null, null);
		// this.validatorService.processAnnotations();
	}

	// @Test
	public void testIsArray() {
		String str = "Hello";
		String atr[][] = new String[10][20];
		System.out.println("Checking for str...");
		checkArray(str);
		System.out.println("Checking for atr...");
		checkArray(atr);
		System.out.println("Checking for empty string array...");
		checkArray(new String[] {});
	}

	private String getFileContents(String fileName) {
		try {
			return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkArray(Object abc) {
		boolean x = abc.getClass().isArray();
		if (x == true) {
			Object[] arr = (Object[]) abc;
			System.out.println("The Object is an Array of size " + arr.length);
		} else {
			System.out.println("The Object is not an Array");
		}
	}
}
