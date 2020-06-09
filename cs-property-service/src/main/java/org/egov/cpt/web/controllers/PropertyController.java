package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Property;
import org.egov.cpt.service.PropertyService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.cpt.web.contracts.PropertyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
public class PropertyController {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/_test")
	public String create() {
		logger.debug("test string response");
		return "test string response";
	}

	@PostMapping("/_create")
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest propertyRequest) {

		List<Property> property = propertyService.createProperty(propertyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(property).responseInfo(resInfo).build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
