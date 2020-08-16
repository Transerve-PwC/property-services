package org.egov.ps.validator.application;

import java.util.List;

import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.IApplicationValidator;
import org.springframework.stereotype.Component;

@ApplicationValidator("length")
@Component
/**
 * A validator that checks the length of stringified value { "type" : "length",
 * "params" : { "min" : 3, "max" : 20 } }
 */
public class LengthValidator implements IApplicationValidator {

    @Override
    public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
        // TODO: Validate based on the params.
        return null;
    }

}