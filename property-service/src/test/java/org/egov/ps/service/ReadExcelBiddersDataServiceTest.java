package org.egov.ps.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.egov.ps.model.Auction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@SuppressWarnings("deprecation")
public class ReadExcelBiddersDataServiceTest {

	
	ReadExcelService readExcelService;
	
	@Before
    public void setup() {
        this.readExcelService = new ReadExcelService();
    }
	
	
	@Test
	public void testReadExcelBidderData() throws FileNotFoundException {
		
		String filepath = "excel/Estate Branch Bidders excel _withComment.xlsx";
		InputStream inputStream = ReadExcelBiddersDataServiceTest.class.getClassLoader().getResourceAsStream(filepath);
		List<Auction> auctions = readExcelService.getDatafromExcel(inputStream, 0);
		Assert.assertTrue(auctions.size()>0);
	}
	
	@Test
	public void testReadExcelBidderDataExtraCoulmn() throws FileNotFoundException {
		
		String filepath = "excel/Estate Branch Bidders excel _withExtraColumn.xlsx";
		InputStream inputStream = ReadExcelBiddersDataServiceTest.class.getClassLoader().getResourceAsStream(filepath);
		List<Auction> auctions = readExcelService.getDatafromExcel(inputStream, 0);
		Assert.assertTrue(auctions.size()>0);
	}
	
	@Test
	public void testReadExcelBidderDataWrongFormat() throws FileNotFoundException {
		
		String filepath = "excel/Sector 52-53 T (Autosaved) (1).xlsx";
		InputStream inputStream = ReadExcelBiddersDataServiceTest.class.getClassLoader().getResourceAsStream(filepath);
		List<Auction> auctions = readExcelService.getDatafromExcel(inputStream, 0);
		Assert.assertTrue(auctions.isEmpty());
	}
}
