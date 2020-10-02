package org.egov.ps.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.egov.ps.model.Auction;
import org.egov.tracer.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ReadExcelService {
	
	private static final String HEADER_CELL = "Auction id";
	private static final String FOOTER_CELL = "Will be same for all users ";
	private static final String[] EXCELNAME = new String[] { "Auction id","Auction Description","List of Participated Bidders in Auction",
			"Deposited EMD amount","Deposit date","EMD validity date","Refund status","Comment"};
	private static final String[] EXCELMAPPINGNAME = new String[] { "id","auctionDescription","participatedBidders","depositedEMDAmount",
			"depositDate","emdValidityDate","refundStatus","comment"};

							

	public List<Auction> getDatafromExcel(InputStream inputStream, int sheetIndex) {
		List<Auction> auctions = new ArrayList<>();
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
						Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						headerCells.add(cell.getRichStringCellValue().getString());
					}
					continue;
				}

				/* Fetching Data will End after this */
				if (FOOTER_CELL.equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
					break;
				}
				
				if (shouldParseRows) {
					Map<String, Object> cellData = new HashedMap<String, Object>();
					for (int cn = 0; cn < currentRow.getLastCellNum(); cn++) {
						Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						if(headerCells.size() > cn) {
							int keyPosition= Arrays.asList(this.EXCELNAME).indexOf(headerCells.get(cn).trim());
							if(keyPosition != -1)
								cellData.put(this.EXCELMAPPINGNAME[keyPosition], getValueFromCell(cell));
						}
					}
					auctions.add(mapper.convertValue(cellData, Auction.class));
				}
				
			}
			if(headerCells.isEmpty()) {
				throw new CustomException(HttpStatus.FORBIDDEN.getReasonPhrase(),
						"Invalid Excel Fomrat,Could not get create workbook or parse data");
			}
		}catch (Exception e) {
			log.error("File reading operation fails due to :" + e.getMessage());
		}
		return auctions;
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
