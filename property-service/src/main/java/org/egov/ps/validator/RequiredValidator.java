package org.egov.ps.validator;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

@ApplicationValidator("required")
@Component
public class RequiredValidator implements IApplicationValidator {

	public RequiredValidator() {

	}

	/**
	 * if string then make sure we have atleast 1 non whitespace character. if
	 * object then make sure it is not null.
	 */
	@Override
	public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			return ((String) value).trim().length() > 0;
		}

		if (value.getClass().isArray()) {

		}
		return true;
	}
}
