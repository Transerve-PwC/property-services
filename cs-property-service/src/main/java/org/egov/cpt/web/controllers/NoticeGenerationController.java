package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.NoticeGeneration;
import org.egov.cpt.service.MortgageService;
import org.egov.cpt.service.NoticeGenerationService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.MortgageRequest;
import org.egov.cpt.web.contracts.MortgageResponse;
import org.egov.cpt.web.contracts.NoticeGenerationRequest;
import org.egov.cpt.web.contracts.NoticeGenerationResponse;
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
@RequestMapping("/notice-generation")
public class NoticeGenerationController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	private NoticeGenerationService noticeGenerationService;


	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 * @param noticeGenerationRequest
	 * @return
	 */
	@PostMapping("/_create")
	public ResponseEntity<NoticeGenerationResponse> create(@Valid @RequestBody NoticeGenerationRequest noticeGenerationRequest) {

		List<NoticeGeneration> noticeGeneration = noticeGenerationService.createNotice(noticeGenerationRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(noticeGenerationRequest.getRequestInfo(),
				true);
		NoticeGenerationResponse response = NoticeGenerationResponse.builder().noticeApplications(noticeGeneration).responseInfo(resInfo)
				.build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<NoticeGenerationResponse> update(@Valid @RequestBody NoticeGenerationRequest noticeGenerationRequest) {

		List<NoticeGeneration> noticeGeneration = noticeGenerationService.updateNotice(noticeGenerationRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(noticeGenerationRequest.getRequestInfo(),
				true);
		NoticeGenerationResponse response = NoticeGenerationResponse.builder().noticeApplications(noticeGeneration).responseInfo(resInfo)
				.build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
