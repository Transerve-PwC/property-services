package org.egov.cpt.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnershipTransferSearchCriteria;
import org.egov.cpt.service.OwnershipTransferService;
import org.egov.cpt.util.ResponseInfoFactory;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.web.contracts.OwnershipTransferResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ownership-transfer")
public class OwnershipTransferController {

	@Autowired
	private OwnershipTransferService ownershipTransferService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<OwnershipTransferResponse> create(
			@Valid @RequestBody OwnershipTransferRequest ownershipTransferRequest) {

		List<Owner> owners = ownershipTransferService.createOwnershipTransfer(ownershipTransferRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(ownershipTransferRequest.getRequestInfo(), true);
		OwnershipTransferResponse response = OwnershipTransferResponse.builder().owners(owners).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/_search")
	public ResponseEntity<OwnershipTransferResponse> search(
			@Valid @RequestBody OwnershipTransferRequest requestInfoWrapper,
			@Valid @ModelAttribute OwnershipTransferSearchCriteria searchCriteria) {

		List<Owner> owners = ownershipTransferService.searchOwnershipTransfer(searchCriteria,
				requestInfoWrapper.getRequestInfo());
		OwnershipTransferResponse response = OwnershipTransferResponse.builder().owners(owners).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<OwnershipTransferResponse> update(
			@Valid @RequestBody OwnershipTransferRequest ownershipTransferRequest) {

		List<Owner> owners = ownershipTransferService.updateOwnershipTransfer(ownershipTransferRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(ownershipTransferRequest.getRequestInfo(), true);
		OwnershipTransferResponse response = OwnershipTransferResponse.builder().owners(owners).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
