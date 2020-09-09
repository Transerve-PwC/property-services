package org.egov.ps.test;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.egov.ps.model.Auction;
import org.egov.ps.service.ReadExcelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReadExcelServiceAuctionsTests {

	ReadExcelService readExcelService;

	@Before
	public void setup() {
		this.readExcelService = new ReadExcelService();
	}
	
	private void _testExcelParsing(String filePath,int sheetNo) {
		try {			
			InputStream inputStream = new FileInputStream(new File(filePath));
			List<Auction> auctions = this.readExcelService.getDatafromExcel(inputStream, sheetNo);
			assertFalse(auctions.size() < 0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			assertFalse(e.getMessage(), false);
		}
	}
	
	@Test
    public void testSheet1() {
		/* Sheet no. 0 */
        String excelFileToParse = "D:\\Projects\\Transerve\\Docs\\Estate Branch Bidders excel .xlsx";
        this._testExcelParsing(excelFileToParse, 0);
    }
	
	
}
