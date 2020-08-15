package org.egov.ps.validator;

import java.util.Map;

import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;

@ApplicationValidator("mdms")
@Component
public class MDMSValidator implements IApplicationValidator {

    @Override
    public boolean isValid(IValidation validation, IApplicationField field, Object value, Object parent) {
        Map<String, Object> params = validation.getParams();
        String moduleName = (String) params.get("moduleName");
        String masterName = (String) params.get("masterName");
        // We need to make a MDMS call.
        // "code"
        // if (value is one of the masters.code)
        return true;
    }

}