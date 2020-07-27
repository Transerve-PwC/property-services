package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.PropertyImages;
import org.egov.cpt.service.PropertyImagesService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.PropertyImagesRequest;
import org.egov.cpt.web.contracts.PropertyImagesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property-images")
public class PropertyImagesController {

	@Autowired
	private PropertyImagesService propertyImagesService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<PropertyImagesResponse> create(
			@Valid @RequestBody PropertyImagesRequest propertyImagesRequest) {

		List<PropertyImages> application = propertyImagesService.createPropertyImages(propertyImagesRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(propertyImagesRequest.getRequestInfo(), true);
		PropertyImagesResponse response = PropertyImagesResponse.builder().propertyImagesApplications(application)
				.responseInfo(resInfo).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	//PI Search API
//	@PostMapping("/_search")
//	public ResponseEntity<PropertyImagesResponse> search(@Valid @RequestBody PropertyImagesRequest requestInfoWrapper,
//			@Valid @ModelAttribute DuplicateCopySearchCriteria searchCriteria) {
//
//		List<PropertyImages> applications = propertyImagesService.searchPropertyImages(searchCriteria,
//				requestInfoWrapper.getRequestInfo());
//		PropertyImagesResponse response = PropertyImagesResponse.builder().propertyImagesApplications(applications)
//				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
//						true))
//				.build();
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@PostMapping("/_update")
	public ResponseEntity<PropertyImagesResponse> update(
			@Valid @RequestBody PropertyImagesRequest propertyImagesRequest) {

		List<PropertyImages> applications = propertyImagesService.updatePropertyImages(propertyImagesRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(propertyImagesRequest.getRequestInfo(), true);
		PropertyImagesResponse response = PropertyImagesResponse.builder().propertyImagesApplications(applications)
				.responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
