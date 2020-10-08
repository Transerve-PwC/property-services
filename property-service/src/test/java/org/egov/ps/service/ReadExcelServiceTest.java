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
	
	@Test
	public void testReadExcelsheet5_verka() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,5,48222407);
	}
	
	@Test
	public void testReadExcelsheet6_LA2() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,6,74992);
	}
	
	@Test
	public void testReadExcelsheet7_LA1() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,7,40680);
	}
	
	@Test
	public void testReadExcelsheet8_Dhanas_2() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,8,81137);
	}
	
	@Test
	public void testReadExcelsheet9_Dhanas_7() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,9,89107);
	}
	@Test
	public void testReadExcelsheet10_Dhanas_1() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,10,79156);
	}
	@Test
	public void testReadExcelsheet11_Dhanas_8() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,11,22840);
	}
	
	@Test
	public void testReadExcelsheet12_Dstore() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,12,12732);
	}
		
	@Test
	public void testReadExcelsheet13_CHD() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,13,30000);
	}
	@Test
	public void testReadExcelsheet14_Dr5() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,14,47770);
	}
	
	@Test
	public void testReadExcelsheet15_dharia_11() throws FileNotFoundException {
		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		testExcelParsing(filepath,15,36719);
	}
	
		@Test
		public void testReadExcelsheet16_dharia_11() throws FileNotFoundException {
			String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
			testExcelParsing(filepath,16,55012);
		}			
				
		@Test
		public void testReadExcelsheet17_DR_8() throws FileNotFoundException {
			String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
			testExcelParsing(filepath,17,55734);
		}	
		
		@Test
		public void testReadExcelsheet18_DR_1() throws FileNotFoundException {
			String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
			testExcelParsing(filepath,18,16812);
		}	
		
		@Test
		public void testReadExcelsheet19_DR_6() throws FileNotFoundException {
			String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
			testExcelParsing(filepath,19,37492);
		}
}
