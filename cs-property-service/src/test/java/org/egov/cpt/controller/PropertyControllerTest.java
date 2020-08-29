package org.egov.cpt.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.egov.cpt.CSPropertyApplication;
import org.egov.cpt.config.TestConfiguration;
import org.egov.cpt.models.Property;
import org.egov.cpt.service.PropertyService;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CSPropertyApplication.class)
@AutoConfigureMockMvc
@Import(TestConfiguration.class)
public class PropertyControllerTest {

	private String timeZone = "UTC";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	PropertyService propertyService;

	@Mock
	PropertyUtil propertyutil;

	@Before
	public void setUp() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Test
	public void CreateRequestTest() throws Exception {
		List<Property> properties = new ArrayList<Property>();
		Property property = Property.builder().id("1").tenantId("ch.chandigarh").transitNumber("trns001")
				.colony("colony").masterDataAction("masterDataAction").masterDataState("masterDataState").build();

		properties.add(property);

		Mockito.when(propertyService.createProperty(Mockito.any(PropertyRequest.class))).thenReturn(properties);
		mockMvc.perform(post("/property/_create").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(getFileContents("createPropertyServiceRequest.json"))).andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(content().json(getFileContents("createPropertyServiceResponse.json")));

	}

	private String getFileContents(String fileName) {
		try {
			return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
