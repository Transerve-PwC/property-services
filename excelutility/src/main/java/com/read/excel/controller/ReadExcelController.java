package com.read.excel.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.read.excel.bean.EmployeeTaxDetails;
import com.read.excel.excelservices.ReadExcelService;
import com.read.excel.utils.FileStoreUtils;

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
	public ResponseEntity<List<EmployeeTaxDetails>> readExcel(@RequestParam(value = "tenantId") String tenantId,
			@RequestParam("fileStoreId") String fileStoreId) {
		log.info("Start controller method readExcel()");
		List<EmployeeTaxDetails> employees = new ArrayList<>();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(tenantId, fileStoreId);
			if (!"".equals(filePath))
				employees = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), "521");
			log.info("End controller method readExcel");
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}
}
