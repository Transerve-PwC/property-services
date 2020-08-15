package org.egov.ps.validator;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;

public interface IApplicationValidator {
	public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent);
}
