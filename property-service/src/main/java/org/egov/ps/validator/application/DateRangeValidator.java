package org.egov.ps.validator.application;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IApplicationValidator;
import org.egov.ps.validator.IValidation;
import org.springframework.stereotype.Component;

/*
 * date-range : Validate based on start and end dates, start and end dates could be strings like “now”, 
 * 																							“1 day ago”, 
 * 																							“6 months ago” 
 * 																							or dates like “01-Jan-2020”, “23-Sep-2020” etc. 
 * example: 
 * { "type" : "date-range", "params" : { "start" : "6 months ago", "end" : "now" } }
 * { "type" : "date-range", "params": { "start" : { "unit" : "month", "value" : -6}, "end": { "unit": "second", "value" : 0 }}}
 * The above validator config should make sure the value is in the last 6 months and no before or no after.
 * 
 */

@ApplicationValidator("date-range")
@Component
public class DateRangeValidator implements IApplicationValidator {

	private static final String DEFAULT_FORMAT = "Invalid Date Range '%s' at path '%s'";
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

	private List<String> formatErrorMessage(String format, Object value, String path) {
		if (format == null) {
			format = DEFAULT_FORMAT;
		}
		return Arrays.asList(String.format(format, value, path));
	}

	@Override
	public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
		// TODO Auto-generated method stub
		if(validation.getType().equalsIgnoreCase("date-range")) {
			if(null != validation.getParams() && validation.getParams().size() > 0) {
				if (!isValid(validation, field, value, parent)) {
					return this.formatErrorMessage(validation.getErrorMessageFormat(), value, field.getPath());
				}
			}else {
				return this.formatErrorMessage(validation.getErrorMessageFormat(), value, field.getPath());
			}

		}
		return null;
	}

	private boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
		String startDateStr = null ;
		String endDateStr = null;

		if(validation.getParams().get("start") instanceof String) {
			startDateStr = validation.getParams().get("start").toString();
		}
		
		if(validation.getParams().get("end") instanceof String) {
			endDateStr = validation.getParams().get("end").toString();
		}
		
		/*if(validation.getParams().get("start") instanceof Object) {
			Object startDt = validation.getParams().get("start");
			Map<String, Object> values = getFieldNamesAndValues(startDt);
			startDateStr = calculateDate(values);
		}
		
		if(validation.getParams().get("end") instanceof Object) {
			Object endDt = validation.getParams().get("start").toString();
			Map<String, Object> values = getFieldNamesAndValues(endDt);
			endDateStr = calculateDate(values);
		}*/

		//###################################

		//checking null of any start or end date then return false.
		if(null == startDateStr || null == endDateStr ) {
			return false;
		}

		//checking start date must be smaller then end date.
		try {
			Date startDate = formatter.parse(startDateStr);
			Date endDate = formatter.parse(endDateStr);

			/* 	start date occurs before end date - if ( startDate.compareTo(endDate) < 0 )
			 * 	start date occurs after end date - if ( startDate.compareTo(endDate) > 0 )
			 * 	Both dates are equal dates - if (startDate.compareTo(endDate) == 0  )
			 */

			if(startDate.compareTo(endDate) > 0 || startDate.compareTo(endDate) == 0 ) {
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String calculateDate(Map<String, Object> values) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getFieldNamesAndValues(Object obj)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Class<? extends Object> c1 = obj.getClass();
			Field[] fields = c1.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				fields[i].setAccessible(true);
				Object value;
				value = fields[i].get(obj);
				map.put(name, value);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}
