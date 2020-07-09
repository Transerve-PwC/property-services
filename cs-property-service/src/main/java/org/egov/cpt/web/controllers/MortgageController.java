package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.service.MortgageService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.MortgageRequest;
import org.egov.cpt.web.contracts.MortgageResponse;
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
@RequestMapping("/mortgage")
public class MortgageController {
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	private MortgageService mortgageService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PostMapping("/_create")
	public ResponseEntity<MortgageResponse> create(@Valid @RequestBody MortgageRequest mortgageRequest) {

		List<Mortgage> mortgage = mortgageService.createApplication(mortgageRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(mortgageRequest.getRequestInfo(),
				true);
		MortgageResponse response = MortgageResponse.builder().mortgageApplications(mortgage).responseInfo(resInfo).build();
		logger.debug("property created sucessfuly");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
