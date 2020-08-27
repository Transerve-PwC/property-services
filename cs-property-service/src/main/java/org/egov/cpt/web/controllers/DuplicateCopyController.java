package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.service.DuplicateCopyService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.DuplicateCopyResponse;
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
	 * 
	 * @param duplicateCopyRequest
	 * @return
	 */
	@PostMapping("/_create")
	public ResponseEntity<DuplicateCopyResponse> create(@Valid @RequestBody DuplicateCopyRequest duplicateCopyRequest) {

		List<DuplicateCopy> application = duplicateCopyService.createApplication(duplicateCopyRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(duplicateCopyRequest.getRequestInfo(), true);
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().duplicateCopyApplications(application)
				.responseInfo(resInfo).build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Search API
	 * 
	 * @param requestInfoWrapper
	 * @param propertyCriteria
	 * @return
	 */
	@PostMapping("/_search")
	public ResponseEntity<DuplicateCopyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute DuplicateCopySearchCriteria searchCriteria) {

		List<DuplicateCopy> applications = duplicateCopyService.searchApplication(searchCriteria,
				requestInfoWrapper.getRequestInfo());
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().duplicateCopyApplications(applications)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Update API
	 * 
	 * @param propertyRequest
	 * @return
	 */
	@PostMapping("/_update")
	public ResponseEntity<DuplicateCopyResponse> update(@Valid @RequestBody DuplicateCopyRequest duplicateCopyRequest) {
		List<DuplicateCopy> applications = duplicateCopyService.updateApplication(duplicateCopyRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(duplicateCopyRequest.getRequestInfo(), true);
		DuplicateCopyResponse response = DuplicateCopyResponse.builder().duplicateCopyApplications(applications)
				.responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
