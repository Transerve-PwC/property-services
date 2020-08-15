package org.egov.ps.model;

import net.minidev.json.JSONObject;

public interface IValidation {

    public String getType();

    public String getErrorMessageFormat();

    public JSONObject getParams();
}