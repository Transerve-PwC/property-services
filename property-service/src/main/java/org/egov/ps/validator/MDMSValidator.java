package org.egov.ps.validator;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;

@ApplicationValidator("mdms")
@Component
public class MDMSValidator implements IApplicationValidator {

    @Override
    public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
        JSONObject params = validation.getParams();
        String moduleName = params.getAsString("moduleName");
        String masterName = params.getAsString("masterName");
        // We need to make a MDMS call.
        // "code"
        // if (value is one of the masters.code)
        return true;
    }

}