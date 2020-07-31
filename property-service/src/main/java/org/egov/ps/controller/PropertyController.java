package org.egov.ps.controller;

import javax.validation.Valid;

import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.ps.web.contracts.PropertyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-master")
public class PropertyController {

	@PostMapping("/_create")
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest propertyRequest) {

//		List<Property> property = propertyService.createProperty(propertyRequest);
//		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
//				true);
//		PropertyResponse response = PropertyResponse.builder().properties(property).responseInfo(resInfo).build();
//		return new ResponseEntity<>(response, HttpStatus.CREATED);
		return null;
	}

}
