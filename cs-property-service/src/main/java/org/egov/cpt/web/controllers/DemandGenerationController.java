package org.egov.cpt.web.controllers;

import javax.validation.Valid;

import org.egov.cpt.models.DemandCriteria;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.service.DemandGenerationService;
import org.egov.cpt.util.ResponseInfoFactory;
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
@RequestMapping("/demand")
public class DemandGenerationController {

	private DemandGenerationService demandGenerationService;

	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	public DemandGenerationController(DemandGenerationService demandGenerationService, ResponseInfoFactory responseInfoFactory) {
		this.demandGenerationService = demandGenerationService;
		this.responseInfoFactory = responseInfoFactory;
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostMapping("/_create")
	public ResponseEntity<?> create(@Valid @ModelAttribute DemandCriteria demandCriteria, @Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		demandGenerationService.createDemand(demandCriteria);
		System.out.println(demandCriteria);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
