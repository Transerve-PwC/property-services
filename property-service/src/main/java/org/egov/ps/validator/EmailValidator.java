package org.egov.ps.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

@ApplicationValidator("email")
@Component
public class EmailValidator implements IApplicationValidator {

	//private static final String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
	private static final String emailRegex = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
	public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
		String email = value.toString();
		
		if(email.split("@").length != 2) {
			return false;
		}
		if(null != email && !email.isEmpty()) {
			Pattern pattern = Pattern.compile(emailRegex);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		}else {
			return false;
		}
	}

}
