package org.egov.ps.controller;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Application;
import org.egov.ps.model.ApplicationCriteria;
import org.egov.ps.service.ApplicationService;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.ApplicationResponse;
import org.egov.ps.web.contracts.RequestInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest applicationRequest) {

		List<Application> applications = applicationService.createApplication(applicationRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(applicationRequest.getRequestInfo(),
				true);
		ApplicationResponse response = ApplicationResponse.builder().applications(applications).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<ApplicationResponse> search(@Valid @RequestBody RequestInfoMapper requestInfoWrapper,
			@Valid @ModelAttribute ApplicationCriteria applicationCriteria) {
		List<Application> applications = applicationService.searchApplication(applicationCriteria,
				requestInfoWrapper.getRequestInfo());
		ApplicationResponse response = ApplicationResponse.builder().applications(applications).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<ApplicationResponse> update(@Valid @RequestBody ApplicationRequest applicationRequest) {

		List<Application> applications = applicationService.updateApplication(applicationRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(applicationRequest.getRequestInfo(),
				true);
		ApplicationResponse response = ApplicationResponse.builder().applications(applications).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
}
