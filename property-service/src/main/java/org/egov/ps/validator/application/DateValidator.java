package org.egov.ps.validator.application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IApplicationValidator;
import org.egov.ps.validator.IValidation;
import org.springframework.stereotype.Component;

@ApplicationValidator("date")
@Component
public class DateValidator implements IApplicationValidator {

	private static final String DEFAULT_FORMAT = "Invalid Date  '%s' at path '%s'";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	
	private List<String> formatErrorMessage(String format, Object value, String path) {
		if (format == null) {
			format = DEFAULT_FORMAT;
		}
		return Arrays.asList(String.format(format, value, path));
	}
	

	@Override
	public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
		// TODO Auto-generated method stub
		
		if(validation.getType().equalsIgnoreCase("date")) {
			boolean isEmpty = value == null || value.toString().trim().length() == 0;
			if (!field.isRequired() || isEmpty) {
				return null;
			}
			String trimmedValue = isEmpty ? null : value.toString().trim();
			if (!isValid(trimmedValue)) {
				return this.formatErrorMessage(validation.getErrorMessageFormat(), value, field.getPath());
			}
		}
		return null;
	}
	
	private static boolean isValid (String strDate) {
		if (null != strDate && !strDate.isEmpty()) {
			try{
				dateFormatter.parse(strDate); 
			}catch (ParseException e){
				return false;
			}
		}else {
			return false;
		}
		return true;		
		
	}
	
	public static void main(String arg[]) {
		System.err.println(isValid("12/29/2016"));
		System.err.println(isValid("16-aug-2020"));
		System.err.println(isValid("01-jan-2020"));
		System.err.println(isValid("01-JAN-2020"));
		System.err.println(isValid(""));
		
	}

}
