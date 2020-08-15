package org.egov.ps.model;

import lombok.Builder;
import lombok.Getter;
import net.minidev.json.JSONObject;

@Getter
@Builder
public class ApplicationValidation implements IValidation {

    private String type;

    private String errorMessageFormat;

    private JSONObject params;

}