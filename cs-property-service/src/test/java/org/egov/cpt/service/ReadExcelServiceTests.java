package org.egov.cpt.service;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemandResponse;
import org.egov.cpt.models.RentPayment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReadExcelServiceTests {

    ReadExcelService readExcelService;

    @Before
    public void setup() {
        this.readExcelService = new ReadExcelService();
    }

    @Test
    public void testSimpleParsing() {
        String excelFileToParse = "calculations/501 to 520.xlsx";
        InputStream inputStream = ReadExcelServiceTests.class.getClassLoader().getResourceAsStream(excelFileToParse);
        RentDemandResponse rentDemandResponse = this.readExcelService.getDatafromExcel(inputStream, 0);
        Double totalRent = rentDemandResponse.getDemand().stream().map(RentDemand::getCollectionPrincipal).collect(Collectors.summingDouble(Double::doubleValue));
        Double totalPaid = rentDemandResponse.getPayment().stream().map(RentPayment::getAmountPaid).collect(Collectors.summingDouble(Double::doubleValue));
        assertEquals(135708.0, totalRent, 1.0);
        assertEquals(123536.0, totalPaid, 1.0);
    }

    @Test
    public void testSheet1() {
        String excelFileToParse = "calculations/501 to 520.xlsx";
        InputStream inputStream = ReadExcelServiceTests.class.getClassLoader().getResourceAsStream(excelFileToParse);
        RentDemandResponse rentDemandResponse = this.readExcelService.getDatafromExcel(inputStream, 1);
        Double totalRent = rentDemandResponse.getDemand().stream().map(RentDemand::getCollectionPrincipal).collect(Collectors.summingDouble(Double::doubleValue));
        Double totalPaid = rentDemandResponse.getPayment().stream().map(RentPayment::getAmountPaid).collect(Collectors.summingDouble(Double::doubleValue));
        assertEquals(135708.0, totalRent, 1.0);
        assertEquals(123035.0, totalPaid, 1.0);
    }

    @Test
    public void testExtractDate() throws ParseException {
        final String[] MONTHS = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        String str = "Aug.-20";
        Pattern monthPattern = Pattern.compile("^\\w*");
		Matcher monthMatcher = monthPattern.matcher(str);
		if(monthMatcher.find()) {
			String month = monthMatcher.group().toUpperCase();
			int monthIndex = Arrays.asList(MONTHS).indexOf(month);
			if (monthIndex < 0 ) {
				throw new DateTimeParseException("Cannot parse "+ str + " as a date.", null, 0);
			}
			Pattern datePattern = Pattern.compile("\\d*$");
			Matcher dateMatcher = datePattern.matcher(str);
			if(dateMatcher.find()) {
                String twoYearDate = dateMatcher.group();
                int twoYearDateInt = Integer.parseInt(twoYearDate);
                if (twoYearDateInt >= 100) {
                    throw new DateTimeParseException("Cannot parse "+ str + " as a date.", null, 0);
                }
                int year = twoYearDateInt < 50 ? 2000 + twoYearDateInt : 1900 + twoYearDateInt;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthIndex, 1);
			}
        }
    }
}