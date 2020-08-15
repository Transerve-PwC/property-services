package org.egov.ps.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;

@ApplicationValidator("phoneNumber")
@Component
public class PhoneNumberValidator implements IApplicationValidator {
	private static final String phoneRegex = "(0/91)?[7-9][0-9]{9}";

	@Override
	public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
		String phoneNumber = value.toString();
		if(null != phoneNumber && !phoneNumber.isEmpty()) {
			Pattern pattern = Pattern.compile(phoneRegex);
			Matcher matcher = pattern.matcher(phoneNumber);
			return matcher.matches();
		}else {
			return false;
		}
	}

}
