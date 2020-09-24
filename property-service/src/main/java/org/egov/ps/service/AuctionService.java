package org.egov.ps.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.Auction;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.model.Property;
import org.egov.ps.producer.Producer;
import org.egov.ps.repository.AuctionRepository;
import org.egov.ps.util.FileStoreUtils;
import org.egov.ps.validator.AuctionValidator;
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.ps.web.contracts.AuctionTransactionRequest;
import org.egov.ps.web.contracts.AutionSearchRequest;
import org.egov.ps.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuctionService {

	@Autowired
	private AuctionRepository auctionRepository;

	@Autowired
	private ReadExcelService readExcelService;

	@Autowired
	private FileStoreUtils fileStoreUtils;
	
	@Autowired
	private Configuration config;

	@Autowired
	private Producer producer;	
	
	@Autowired
	AuctionValidator auctionValidator;
	
	@Autowired
	private EnrichmentService enrichmentService;	

	public List<Auction> saveAuctionWithProperty(ExcelSearchCriteria searchCriteria, AuctionTransactionRequest auctionTransactionRequest) {		
		AuctionSaveRequest request = enrichmentService.enrichAuctionCreateRequest(searchCriteria,auctionTransactionRequest);
		producer.push(config.getSaveAuctionTopic(), request);
		return request.getAuctions();
	}

	public List<Auction> searhAuctionMaster(AutionSearchRequest autionSearchRequest) {
		List<Auction> auctions = auctionRepository.search(autionSearchRequest.getAuctionSearchCritirea());
		return auctions;
	}


	/**
	 * Updates the Auction
	 *
	 * @param request AuctionSaveRequest containing list of auction to be update
	 * @return List of updated auction
	 */
	public List<Auction> updateAuction(AuctionSaveRequest auctionSaveRequest) {
		List<Auction> auctionFromSearch = auctionValidator.validateAuctionUpdate(auctionSaveRequest);
		enrichmentService.enrichUpdateAuctionRequest(auctionSaveRequest, auctionFromSearch);
		producer.push(config.getUpdateAuctionTopic(), auctionSaveRequest);
		return auctionSaveRequest.getAuctions();
	}

}
