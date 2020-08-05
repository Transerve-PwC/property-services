package org.egov.ps.controller;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.model.RequestInfoMapper;
import org.egov.ps.service.PropertyService;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.ps.web.contracts.PropertyResponse;
import org.egov.ps.web.contracts.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-master")
public class PropertyController {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest propertyRequest) {

		List<Property> property = propertyService.createProperty(propertyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(property).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<PropertyResponse> update(@Valid @RequestBody PropertyRequest propertyRequest) {
		List<Property> properties = propertyService.updateProperty(propertyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<SearchResponse> search(@Valid @RequestBody RequestInfoMapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria propertyCriteria) {

		List<Object> properties = propertyService.searchProperty(propertyCriteria,
				requestInfoWrapper.getRequestInfo());
		SearchResponse response = SearchResponse.builder().properties(properties).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
//		String modelName =  response.getProperties().get(0).getClass().getName();
//		if (modelName.equals("org.egov.ps.Owner")) {
//			response.r;
//		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
