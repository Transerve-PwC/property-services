package org.egov.cpt.web.controllers;

import javax.validation.Valid;

import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.ExcelSearchCriteria;
import org.egov.cpt.models.RentDemandResponse;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.service.ReadExcelService;
import org.egov.cpt.util.FileStoreUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/v1/excel")
public class ReadExcelController {

	private ReadExcelService readExcelService;
	private FileStoreUtils fileStoreUtils;

	public ReadExcelController(ReadExcelService readExcelService, FileStoreUtils fileStoreUtils) {
		super();
		this.readExcelService = readExcelService;
		this.fileStoreUtils = fileStoreUtils;
	}

	@PostMapping("/read")
	public ResponseEntity<RentDemandResponse> readExcel(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute ExcelSearchCriteria searchCriteria) {
		log.info("Start controller method readExcel()");
		RentDemandResponse data = new RentDemandResponse();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!"".equals(filePath))
				data = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0);
			log.info("End controller method readExcel");
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
}
