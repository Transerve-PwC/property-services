package com.read.excel.excelservices;

import java.io.File;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.excel.bean.EmployeeTaxDetails;

@Service
public class ReadExcelService {

	public static List<EmployeeTaxDetails> getDatafromExcel(String filePath,String sheetName) {
		List<EmployeeTaxDetails> employees = new ArrayList<>();
		try {
			File tempFile = new File(filePath);
			Workbook workbook = WorkbookFactory.create(tempFile);
			System.out.println(workbook.getNumberOfSheets());
			Sheet sheet = workbook.getSheet(sheetName);
			Iterator<Row> rowIterator = sheet.iterator();
			int count = 0;

			List<String> headerCells = new ArrayList<>(); 
			while (rowIterator.hasNext()) {
				Row currentRow = rowIterator.next();
				/* Fetching Data will Start after this */
				if ("Month".equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {	
					headerCells = new ArrayList<>();
					for (int cn = 0; cn < currentRow.getLastCellNum(); cn++) {
						Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						headerCells.add(cell.getRichStringCellValue().getString());
					}
					currentRow = rowIterator.next();
					count++;
				}
				if (count > 0) {
					Map<String, Object> cellData = new HashedMap<String, Object>();					
					for (int cn = 0; cn < currentRow.getLastCellNum(); cn++) {
						Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						if(headerCells.size() == currentRow.getLastCellNum()) {
							cellData.put(headerCells.get(cn), getValueFromCell(cell));
						}
					}
					ObjectMapper mapper = new ObjectMapper();
					employees.add(mapper.convertValue(cellData, EmployeeTaxDetails.class));
					/* Fetching Data will End after this */
					if ("Total".equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
						break;
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employees;
	}

	private static Object getValueFromCell(Cell cell1) {
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
					objValue = cell1.getDateCellValue();
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

	public static void main(String args[]) {
		List<EmployeeTaxDetails> temps = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","521");
		System.out.println(temps.get(temps.size()-1));
		List<EmployeeTaxDetails> temps1 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","522");
		System.out.println(temps1.get(temps1.size()-1));

	}
}
