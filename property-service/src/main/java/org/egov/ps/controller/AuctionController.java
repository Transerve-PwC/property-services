package org.egov.ps.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.service.AuctionService;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.ps.web.contracts.AuctionSearhResponse;
import org.egov.ps.web.contracts.AuctionTransactionRequest;
import org.egov.ps.web.contracts.AuctionTransactionResponse;
import org.egov.ps.web.contracts.AutionSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
public class AuctionController {
	
	@Autowired
	private AuctionService auctionService;	
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_create")
	public ResponseEntity<AuctionTransactionResponse> create(@Valid @ModelAttribute ExcelSearchCriteria searchCriteria,
			@Valid @RequestBody AuctionTransactionRequest auctionTransactionRequest) {
		
		List<Auction> auctions = auctionService.saveAuctionWithProperty(searchCriteria,auctionTransactionRequest);		
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(auctionTransactionRequest.getRequestInfo(),
				true);
		AuctionTransactionResponse response = AuctionTransactionResponse.builder().auctions(auctions)
				.responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<AuctionSearhResponse> searh(@Valid @RequestBody AutionSearchRequest autionSearchRequest) {
		List<Auction> auctions = new ArrayList<>();
		auctions = auctionService.searhAuctionMaster(autionSearchRequest);		
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(autionSearchRequest.getRequestInfo(),
				true);
		AuctionSearhResponse response = AuctionSearhResponse.builder().auctions(auctions).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<AuctionTransactionResponse> update(@Valid @RequestBody AuctionSaveRequest auctionSaveRequest) {
		List<Auction> auctions = auctionService.updateAuction(auctionSaveRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(auctionSaveRequest.getRequestInfo(),
				true);
		AuctionTransactionResponse response = AuctionTransactionResponse.builder().auctions(auctions)
				.responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
