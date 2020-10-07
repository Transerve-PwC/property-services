package org.egov.ps.service;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.egov.ps.web.contracts.EstateModuleResponse;
import org.egov.ps.web.contracts.EstatePayment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@SuppressWarnings("deprecation")
public class ReadExcelServiceTest {

	EstateCalculationExcelReadService estateCalculationExcelReadService;

	@Before
	public void setup() {
		this.estateCalculationExcelReadService = new EstateCalculationExcelReadService();
	}

	private void testExcelParsing(String excelFileToParse, int sheetNo, double expectedReceivedRent) {
		
		InputStream inputStream = ReadExcelServiceTest.class.getClassLoader().getResourceAsStream(excelFileToParse);
		EstateModuleResponse responsne = estateCalculationExcelReadService.getDatafromExcel(inputStream, sheetNo);
		Double rentReceived = responsne.getEstatePayments().stream().map(EstatePayment::getRentReceived)
							 .collect(Collectors.summingDouble(Double::doubleValue));			
		assertEquals(expectedReceivedRent, rentReceived, 1.0);
	}
	
	@Test
	public void testReadExcelsheet0() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,0,47770);
	}
	
	@Test
	public void testReadExcelsheet1() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,1,13200);
	}
	
	@Test
	public void testReadExcelsheet4() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,4,3923520);
	}
}
