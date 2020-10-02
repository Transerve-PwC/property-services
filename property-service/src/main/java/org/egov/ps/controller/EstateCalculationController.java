package org.egov.ps.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.EstateCalculationModel;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.service.EnrichmentService;
import org.egov.ps.service.EstateCalculationExcelReadService;
import org.egov.ps.util.FileStoreUtils;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.EstateCalculationResponse;
import org.egov.ps.web.contracts.RequestInfoMapper;
import org.egov.tracer.model.CustomException;
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
@RequestMapping("/estate")
public class EstateCalculationController {

	@Autowired
	private EstateCalculationExcelReadService estateCalculationExcelReadService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private FileStoreUtils fileStoreUtils;

	@PostMapping("/_calculation")
	public ResponseEntity<EstateCalculationResponse> estateCalculation(@Valid @ModelAttribute ExcelSearchCriteria searchCriteria,
			@Valid @RequestBody RequestInfoMapper requestInfoWrapper) {
		List<EstateCalculationModel> estateCalculations = new ArrayList<>();
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!filePath.isEmpty()) {
				estateCalculations = estateCalculationExcelReadService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0);
			}
			ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
			EstateCalculationResponse response = EstateCalculationResponse.builder()
					.estateCalculationModels(estateCalculations).responseInfo(resInfo).build();
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occur during runnig controller method estateCalculation():" + e.getMessage());
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
					"Error Occur during file reading operation or Invalid file credentials.");
		}
	}
}
