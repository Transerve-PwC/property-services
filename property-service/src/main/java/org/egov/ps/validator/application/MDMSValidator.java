package org.egov.ps.validator.application;

import java.util.List;
import java.util.Map;

import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.IApplicationValidator;
import org.springframework.stereotype.Component;

@ApplicationValidator("mdms")
@Component
public class MDMSValidator implements IApplicationValidator {

    @Override
    public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
        Map<String, Object> params = validation.getParams();
        String moduleName = (String) params.get("moduleName");
        String masterName = (String) params.get("masterName");
        // We need to make a MDMS call.
        // "code"
        // if (value is one of the masters.code)
        return null;
    }

}