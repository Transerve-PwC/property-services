package org.egov.cpt.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.Role;
import org.egov.cpt.CSPropertyApplication;
import org.egov.cpt.models.Property;
import org.egov.cpt.service.PropertyService;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CSPropertyApplication.class)
public class PropertyServiceValidationTest {
	@Autowired
	private PropertyService propertyService;

	@Autowired
	private PropertyValidator propertyValidator;

	private ObjectMapper objectMapper ;

	private String timeZone="UTC";

	@Before
	public void setUp(){
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
		objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setTimeZone(TimeZone.getTimeZone(timeZone));
	}

	@Test
	public void CreateRequestTest() throws Exception{
		PropertyRequest propertyRequest = this.objectMapper.readValue(getFileContents("createPropertyServiceRequest.json"), PropertyRequest.class);
		List<Property> propertyList = propertyService.createProperty(propertyRequest);
		assertFalse(propertyList.isEmpty());
		assertEquals(propertyList.get(0).getTenantId(),"ch.chandigarh");
		assertEquals(propertyList.get(0).getTransitNumber(),propertyRequest.getProperties().get(0).getTransitNumber());
	}

	/**
	 * when mandatory fields missing
	 * @throws Exception
	 */
	@Test(expected= CustomException.class)
	public void validateCreateRequest() throws Exception{
		PropertyRequest propertyRequest = this.objectMapper.readValue(getFileContents("mandatoryFieldBadRequest.json"), PropertyRequest.class);
		propertyValidator.validateCreateRequest(propertyRequest);
	}
	/**
	 * DuplicateTransit Number
	 * @throws Exception
	 */
	@Test(expected= CustomException.class)
	public void duplicateTransitIdTest() throws Exception{
//		when(propertyValidator.isTransitIdPresent("Tran000", "ch.chandigarh")).thenReturn(true);
		PropertyRequest propertyRequest = this.objectMapper.readValue(getFileContents("duplicateTransitIdRequest.json"), PropertyRequest.class);
		propertyValidator.validateCreateRequest(propertyRequest);
	}


	private String getFileContents(String fileName) {
		try {
			return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
