package org.egov.cpt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
//import org.egov.cpt.models.paymentcalculation.Demand;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemandResponse;
import org.egov.cpt.models.RentPayment;
//import org.egov.cpt.models.paymentcalculation.DemandPaymentResponse;
//import org.egov.cpt.models.paymentcalculation.Payment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReadExcelService {

	public RentDemandResponse getDatafromExcelPath(String filePath) {
		RentDemandResponse response = new RentDemandResponse();
		try {
			response =  getDatafromExcel(new FileInputStream(new File(filePath)));
		} catch (FileNotFoundException e) {
			log.error("File converting inputstream operation failed due to :" + e.getMessage());
		}
		return response;
	}

	public RentDemandResponse getDatafromExcel(InputStream inputStream) {
		List<RentDemand> demands = new ArrayList<>();
		List<RentPayment> payments = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
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
				
				/* Fetching Data will End after this */
				if ("Total".equalsIgnoreCase(String.valueOf(currentRow.getCell(0)))) {
					break;
				}
				
				if (count > 0) {
					Map<String, Object> cellData = new HashedMap<String, Object>();
					for (int cn = 0; cn < currentRow.getLastCellNum(); cn++) {
						Cell cell = currentRow.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						if(headerCells.size() > cn) {
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
							Date date = new Date (Long.parseLong( cellData.get("Month").toString() ) * 1000);
							date.setDate(Integer
									.parseInt(receiptDetails[receiptDetails.length - 1].split("[\\/s@&.?$+-]+")[0]));
							cellData.put("Receipt Date", cellData.get("Month").toString());
						}
						
					}
					cellData.remove("Receipt No. & Date");
					if(cellData.get("Realization Amount") != null && 
							Double.parseDouble(cellData.get("Realization Amount").toString()) > 0) {
						payments.add(mapper.convertValue(cellData, RentPayment.class));
					}else if(cellData.get("Realization Amount") != null &&
							Double.parseDouble(cellData.get("Realization Amount").toString()) == 0) {
						demands.add(mapper.convertValue(cellData, RentDemand.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("File reading operation fails due to :" + e.getMessage());
		}
		return new RentDemandResponse(demands,payments);
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

	public void main(String args[]) {
		
		RentDemandResponse temps = getDatafromExcelPath("D:\\Projects\\Transerve\\Docs\\521 to 530.xlsx");
		System.out.println(temps);

	}
}
