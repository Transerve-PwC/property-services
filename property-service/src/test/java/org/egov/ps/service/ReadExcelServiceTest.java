package org.egov.ps.service;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReadExcelServiceTest {

	EstateCalculationExcelReadService estateCalculationExcelReadService;

	@Before
	public void setup() {
		this.estateCalculationExcelReadService = new EstateCalculationExcelReadService();
	}

	@Test
	public void testReadExcelWithNewFormat() throws FileNotFoundException {

		String filepath = "excel/Estate Module Revised Calculation sheet.xlsx";
		InputStream inputStream = ReadExcelServiceTest.class.getClassLoader().getResourceAsStream(filepath);
		estateCalculationExcelReadService.getDatafromExcel(inputStream, 0);

	}
}
