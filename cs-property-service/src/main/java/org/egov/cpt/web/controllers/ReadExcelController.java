package org.egov.cpt.web.controllers;

import org.egov.cpt.models.RentDemandResponse;
import org.egov.cpt.service.ReadExcelService;
import org.egov.cpt.util.FileStoreUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/read")
	public ResponseEntity<RentDemandResponse> readExcel(@RequestParam(value = "tenantId") String tenantId,
			@RequestParam("fileStoreId") String fileStoreId) {
		log.info("Start controller method readExcel()");
		RentDemandResponse data = new RentDemandResponse();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(tenantId, fileStoreId);
			if (!"".equals(filePath))
				data = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream());
			log.info("End controller method readExcel");
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
}
