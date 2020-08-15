package org.egov.ps.test.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
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
		DocumentContext applicationObjectContext = JsonPath.parse(simpleApplicationObjectJSON);

		/**
		 * if in the future mdmsService.getApplicationConfig is called, then return
		 * `result`
		 */
		Mockito.when(
				mdmsService.getApplicationConfig(eq(SampleApplicationType), any(RequestInfo.class), any(String.class)))
				.thenReturn(result);
		this.validatorService.performValidationsFromMDMS(SampleApplicationType, applicationObjectContext, null, null);
		// this.validatorService.processAnnotations();
	}

	@Test
	public void testSimpleJSONPath() {
		String simpleApplicationObjectJSON = getFileContents("simpleApplicationObject.json");
		System.out.println(simpleApplicationObjectJSON);
		DocumentContext documentContext = JsonPath.parse(simpleApplicationObjectJSON);
		Map<String, Object> purchaser = documentContext.read("$.purchaser");
		System.out.println("purchaser: " + purchaser);
		String purchaserName = documentContext.read("$.purchaser.name");
		assertEquals("foo", purchaserName);

		String modeOfTransfer = documentContext.read("modeOfTransfer");
		System.out.println("modeOfTransfer: " + modeOfTransfer);
		assertEquals("someValue", modeOfTransfer);
	}

	@Test
	public void testArrayJSONPath() {
		String simpleApplicationObjectJSON = getFileContents("testJSONPath.json");
		System.out.println(simpleApplicationObjectJSON);
		DocumentContext documentContext = JsonPath.parse(simpleApplicationObjectJSON);
		List<String> ownerNames = documentContext.read("$.owners.*.name");
		assertEquals("Ramu", ownerNames.get(0));
		assertEquals("Shamu", ownerNames.get(1));
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
