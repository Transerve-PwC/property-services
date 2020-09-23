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
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.ps.web.contracts.AuctionTransactionRequest;
import org.egov.ps.web.contracts.AutionSearchRequest;
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
	private EnrichmentService enrichmentService;	

	public List<Auction> saveAuctionWithMaster(ExcelSearchCriteria searchCriteria, AuctionTransactionRequest auctionTransactionRequest) {
		Property property = auctionTransactionRequest.getProperty();
		List<Auction> auctions = new ArrayList<>();
		AuctionSaveRequest request = AuctionSaveRequest.builder()
				.requestInfo(auctionTransactionRequest.getRequestInfo()).build();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!filePath.isEmpty()) {
				auctions = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0);
				auctions.forEach(auction -> {
					auction.setPropertyId(property.getId());
					auction.setTenantId(property.getTenantId());
					auction.setFileNumber(property.getFileNumber());
				});				
			}
			request.setAuctions(auctions);
			enrichmentService.enrichAuctionCreateRequest(request);
			producer.push(config.getSaveAuctionTopic(), request);
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return request.getAuctions();
	}

	public List<Auction> searhAuctionMaster(AutionSearchRequest autionSearchRequest) {
		List<Auction> auctions = auctionRepository.searchByFileNumber(autionSearchRequest);
		return auctions;
	}

}
