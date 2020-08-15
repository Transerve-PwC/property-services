package org.egov.ps.validator;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

@ApplicationValidator("enum")
@Component
public class EnumValidator implements IApplicationValidator {

    @Override
    public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
        System.out.println("Enum validator needs to validate " + value);
        return false;
    }

}