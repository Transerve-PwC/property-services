package org.egov.ps.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.ps.model.Auction;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.service.ReadExcelService;
import org.egov.ps.util.FileStoreUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/excel")
public class ReadExcelController {

	private ReadExcelService readExcelService;
	private FileStoreUtils fileStoreUtils;

	public ReadExcelController(ReadExcelService readExcelService, FileStoreUtils fileStoreUtils) {
		super();
		this.readExcelService = readExcelService;
		this.fileStoreUtils = fileStoreUtils;
	}

	@GetMapping("/read")
	public ResponseEntity<Object> readExcel(@Valid @ModelAttribute ExcelSearchCriteria searchCriteria) {
		log.info("Start controller method readExcel()");
		List<Auction> auctions = new ArrayList<>();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!"".equals(filePath))
				auctions = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0);
			log.info("End controller method readExcel");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return new ResponseEntity<>(auctions, HttpStatus.OK);
	}
}
