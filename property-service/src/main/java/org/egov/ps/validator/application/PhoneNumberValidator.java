package org.egov.ps.validator.application;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.IApplicationValidator;
import org.springframework.stereotype.Component;

@ApplicationValidator("mobile")
@Component
public class PhoneNumberValidator implements IApplicationValidator {
	private static final String phoneRegex = "(0/91)?[7-9][0-9]{9}";

	private static String DEFAULT_ERROR_FORMAT = "Valid phone number expected for path %s but found %s";

	@Override
	public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
		String phoneNumber = value.toString();
		Pattern pattern = Pattern.compile(phoneRegex);
		Matcher matcher = pattern.matcher(phoneNumber);
		if (!matcher.matches()) {
			return Arrays.asList(String.format(DEFAULT_ERROR_FORMAT, field.getPath(), value));
		}
		return null;
	}
}
