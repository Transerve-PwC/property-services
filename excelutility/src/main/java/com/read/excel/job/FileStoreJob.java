package com.read.excel.job;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.read.excel.bean.EmployeeTaxDetails;
import com.read.excel.excelservices.ReadExcelService;
import com.read.excel.utils.FileStoreUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileStoreJob {

	private ReadExcelService readExcelService;
	private FileStoreUtils fileStoreUtils;

	public FileStoreJob(ReadExcelService readExcelService, FileStoreUtils fileStoreUtils) {
		super();
		this.readExcelService = readExcelService;
		this.fileStoreUtils = fileStoreUtils;
	}

//	@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
//	public void excelDataDbOperations() {
//		log.info("Start Job fileDataDbOperations");
//		try {
//			String filePath = fileStoreUtils.fetchFileStoreUrl("ch", "17b8523a-879c-4c84-a41e-174903716137");
//			if (!"".equals(filePath)) {
//				List<EmployeeTaxDetails> employees = readExcelService
//						.getDatafromExcel(new UrlResource(filePath).getInputStream(), "521");
//				System.out.println(employees.size());
//			}
//		} catch (Exception e) {
//			log.error("Error Occur during runnig Job fileDataDbOperations():"+e.getMessage());
//		}
//		log.info("End Job fileDataDbOperations");
//	}
}
