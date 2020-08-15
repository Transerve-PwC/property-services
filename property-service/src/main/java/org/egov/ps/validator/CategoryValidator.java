package org.egov.ps.validator;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

@Component
@ApplicationValidator("component")
public class CategoryValidator implements IApplicationValidator {

    @Override
    public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {

        return false;
    }

}