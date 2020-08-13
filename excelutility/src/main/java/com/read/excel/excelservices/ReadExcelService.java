package com.read.excel.excelservices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReadExcelService {

	public List<EmployeeTaxDetails> getDatafromExcelPath(String filePath, String sheetName) {
		List<EmployeeTaxDetails> employees = new ArrayList<>();
		try {
			employees =  getDatafromExcel(new FileInputStream(new File(filePath)), sheetName);
		} catch (FileNotFoundException e) {
			log.error("File converting inputstream operation failed due to :" + e.getMessage());
		}
		return employees;
	}

	public List<EmployeeTaxDetails> getDatafromExcel(InputStream inputStream, String sheetName) {
		List<EmployeeTaxDetails> employees = new ArrayList<>();
		try {
			Workbook workbook = WorkbookFactory.create(inputStream);
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
						if (headerCells.size() == currentRow.getLastCellNum()) {
							cellData.put(headerCells.get(cn), getValueFromCell(cell));
						}
					}
					cellData.put("Receipt No", "");
					cellData.put("Receipt Date", "");
					if (cellData.get("Receipt No. & Date") != null
							&& !"".equalsIgnoreCase(String.valueOf(cellData.get("Receipt No. & Date")))) {
						String[] receiptDetails = cellData.get("Receipt No. & Date").toString().split(" ");
						cellData.put("Receipt No", receiptDetails[0]);
						if (receiptDetails.length > 1) {
							Date date = new SimpleDateFormat("dd-MM-yyyy").parse(cellData.get("Month").toString());
							date.setDate(Integer
									.parseInt(receiptDetails[receiptDetails.length - 1].split("[\\/s@&.?$+-]+")[0]));
							cellData.put("Receipt Date", convertDateinSameFormat(date));
						}
					}
					cellData.remove("Receipt No. & Date");
					ObjectMapper mapper = new ObjectMapper();
					employees.add(mapper.convertValue(cellData, EmployeeTaxDetails.class));
					/* Fetching Data will End after this */
					if ("Total".equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("File reading operation fails due to :" + e.getMessage());
		}
		return employees;
	}

	private String convertDateinSameFormat(Date date) {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
		return DATE_FORMAT.format(date);
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
				objValue = convertDateinSameFormat(cell1.getDateCellValue());
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
//		List<EmployeeTaxDetails> temps = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","521");
//		System.out.println(temps);
//		List<EmployeeTaxDetails> temps1 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","522");
//		System.out.println(temps1);
//		List<EmployeeTaxDetails> temps2 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","523");
//		System.out.println(temps2);
//		List<EmployeeTaxDetails> temps3 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","524");
//		System.out.println(temps3);
//		List<EmployeeTaxDetails> temps4 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","525");
//		System.out.println(temps4);
//		List<EmployeeTaxDetails> temps5 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","526");
//		System.out.println(temps5);
//		List<EmployeeTaxDetails> temps6 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","527");
//		System.out.println(temps6);
//		List<EmployeeTaxDetails> temps7 = getDatafromExcel("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx","528");
//		System.out.println(temps7);
	}
}
