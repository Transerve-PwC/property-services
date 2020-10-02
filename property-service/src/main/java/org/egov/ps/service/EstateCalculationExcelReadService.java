package org.egov.ps.service;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.egov.ps.model.EstateCalculationModel;
import org.javers.common.collections.Arrays;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EstateCalculationExcelReadService {

	private static final String HEADER_CELL = "Month";
	private static final String FOOTER_CELL = "Total";
	private static final int[] REQUIRED_COLUMNS = { 0, 1, 5, 6, 11, 12, 13, 14, 17, 18, 21, 22 };
	private static final String[] EXCELMAPPINGNAME = new String[] {"month","rentDue","rentReceiptNo","date","penaltyInterest","stGstRate",
													 "stGstDue","paid","dateOfReceipt","stGstReceiptNo","noOfDays","delayedPaymentOfGST"};
	private static final String[] MONTHS = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP",
			"OCT", "NOV", "DEC" };
	private static final DecimalFormat DOUBLE_RISTRICT = new DecimalFormat("#.##");
	private static int SKIP_ROW_COUNT = 2;

	public List<EstateCalculationModel> getDatafromExcel(InputStream inputStream, int sheetIndex) {
		List<EstateCalculationModel> estateCalculations = new ArrayList<>();
		try {
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			Iterator<Row> rowIterator = sheet.iterator();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			boolean shouldParseRows = false;
			List<String> headerCells = new ArrayList<>();
			while (rowIterator.hasNext()) {
				Row currentRow = rowIterator.next();
				
				if (HEADER_CELL.equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
					shouldParseRows = true;
					headerCells = new ArrayList<>();
					for (int cn = 0; cn < currentRow.getLastCellNum(); cn++) {
						if(Arrays.asList(REQUIRED_COLUMNS).contains(cn)) {
							Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
							headerCells.add(cell.getRichStringCellValue().getString());
						}
					}
					continue;
				}
				
				if(shouldParseRows && SKIP_ROW_COUNT > 0) {
					SKIP_ROW_COUNT--;
					continue;
				}
				
				/* Fetching Data will End after this */
				if (FOOTER_CELL.equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
					break;
				}
				
				if (shouldParseRows && SKIP_ROW_COUNT == 0) {
					Map<String, Object> cellData = new HashedMap<String, Object>();
					int headerCount =0;
					/* Fetching Body Data will read after this */
					for(int columnNumber : REQUIRED_COLUMNS) {
						Cell cell = currentRow.getCell(columnNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						if(columnNumber == 6) {
							cellData.put(EXCELMAPPINGNAME[headerCount], extractDateFromString(String.valueOf(getValueFromCell(cell))));
						}else {
							cellData.put(EXCELMAPPINGNAME[headerCount], getValueFromCell(cell));
						}																							
						headerCount++;
					}
					estateCalculations.add(mapper.convertValue(cellData, EstateCalculationModel.class));
				}				
			}
			estateCalculations.forEach(estateCalculation->{
				estateCalculation.setStGstDue(calculateSTGSTDue(estateCalculation));
				estateCalculation.setDelayedPaymentOfGST(calculateDelayedPayment(estateCalculation));
				estateCalculation.setStGstRate(estateCalculation.getStGstRate()*100);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return estateCalculations;
	}
	
	private Double calculateSTGSTDue(EstateCalculationModel estateCalculationModel){
		return Double.parseDouble(DOUBLE_RISTRICT.format(estateCalculationModel.getRentDue()*estateCalculationModel.getStGstRate()));
	}
	
	private Double calculateDelayedPayment(EstateCalculationModel estateCalculationModel){
		return Double.parseDouble(DOUBLE_RISTRICT.format((estateCalculationModel.getStGstDue()*estateCalculationModel.getStGstRate()*estateCalculationModel.getNoOfDays())/365));
	}

	/**
	 * Parse values like 8.4.19
	 * 
	 * @param str
	 * @return
	 * @throws DateTimeParseException
	 */
	private Long extractDateFromString(String str) throws DateTimeParseException {
		if(!str.isEmpty()) {
			String[] splittedDate = str.split("\\.");
			if (splittedDate.length == 3) {
				int monthIndex = Integer.parseInt(splittedDate[1])-1;
				Pattern datePattern = Pattern.compile("\\d*$");
				Matcher dateMatcher = datePattern.matcher(str);
				if (dateMatcher.find()) {
					String twoYearDate = dateMatcher.group();
					int twoYearDateInt = Integer.parseInt(twoYearDate);
					if (twoYearDateInt >= 100) {
						throw new DateTimeParseException("Cannot parse " + str + " as a date.", "", 0);
					}
					int year = twoYearDateInt < 50 ? 2000 + twoYearDateInt : 1900 + twoYearDateInt;
					Calendar calendar = Calendar.getInstance();
					calendar.set(year, monthIndex, Integer.parseInt(splittedDate[0]), 12, 0);
					return calendar.getTimeInMillis();
				}
			}
			throw new DateTimeParseException("Cannot parse " + str + " as a date.", "", 0);
		}
		return null;
	}
		
	private Object getValueFromCell(Cell cell1) {
		Object objValue = "";
		switch (cell1.getCellType()) {
			case BLANK:
				objValue = "";
				break;
			case STRING:
				objValue = cell1.getRichStringCellValue().getString();
				break;
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell1)) {
					objValue = cell1.getDateCellValue().getTime();
				} else {
					objValue = cell1.getNumericCellValue();
				}
				break;
			case FORMULA:
				objValue = cell1.getNumericCellValue();
				break;

			default:
				objValue = "";
		}
		return objValue;
	}
}
