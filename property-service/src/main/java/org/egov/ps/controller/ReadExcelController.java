package org.egov.ps.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.service.AuctionService;
import org.egov.ps.service.ReadExcelService;
import org.egov.ps.util.FileStoreUtils;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.ApplicationResponse;
import org.egov.ps.web.contracts.AuctionResponse;
import org.egov.ps.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/excel")
public class ReadExcelController {

	private ReadExcelService readExcelService;
	private FileStoreUtils fileStoreUtils;
	private AuctionService auctionService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	public ReadExcelController(ReadExcelService readExcelService, FileStoreUtils fileStoreUtils,
			AuctionService auctionService) {
		this.readExcelService = readExcelService;
		this.fileStoreUtils = fileStoreUtils;
		this.auctionService = auctionService;
	}

	@PostMapping("/read")
	public ResponseEntity<Object> readExcel(@Valid @ModelAttribute ExcelSearchCriteria searchCriteria,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		boolean requestStatus= false;
		log.info("Start controller method readExcel()");
		List<Auction> auctions = new ArrayList<>();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!filePath.isEmpty()) {
				auctions = auctionService.createAuctions(readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0),
						requestInfoWrapper.getRequestInfo());
				requestStatus =true;
			} else 
				log.error("Error,No file is avaliable for provided credentials:" + requestInfoWrapper);
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
			}
		  log.info("End controller method readExcel auctions :"+auctions.size());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),requestStatus);
		AuctionResponse response = AuctionResponse.builder().auctions(auctions).responseInfo(resInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
