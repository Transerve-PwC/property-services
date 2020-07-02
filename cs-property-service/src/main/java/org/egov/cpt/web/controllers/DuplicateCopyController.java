package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.service.DuplicateCopyService;
import org.egov.cpt.service.PropertyService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.DuplicateCopyResponse;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.cpt.web.contracts.PropertyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/duplicatecopy")
public class DuplicateCopyController {
	@Autowired
	private DuplicateCopyService duplicateCopyService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * Create API
	 * @param duplicateCopyRequest
	 * @return
	 */
	@PostMapping("/_create")
	public ResponseEntity<DuplicateCopyResponse> create(@Valid @RequestBody DuplicateCopyRequest duplicateCopyRequest) {

		List<DuplicateCopy> property = duplicateCopyService.createProperty(duplicateCopyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(duplicateCopyRequest.getRequestInfo(),
				true);
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().properties(property).responseInfo(resInfo).build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Search API
	 * @param requestInfoWrapper
	 * @param propertyCriteria
	 * @return
	 */
	@PostMapping("/_search")
	public ResponseEntity<DuplicateCopyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria propertyCriteria) {

		List<DuplicateCopy> properties = duplicateCopyService.searchProperty(propertyCriteria,requestInfoWrapper.getRequestInfo());
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().properties(properties).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true)).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Update API
	 * @param propertyRequest
	 * @return
	 */
	@PostMapping("/_update")
	public ResponseEntity<DuplicateCopyResponse> update(@Valid @RequestBody DuplicateCopyRequest duplicateCopyRequest) {
		List<DuplicateCopy> properties = duplicateCopyService.updateProperty(duplicateCopyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(duplicateCopyRequest.getRequestInfo(),
				true);
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().properties(properties).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}


