package org.egov.ps.model;

import java.util.Map;

public interface IValidation {

    public String getType();

    public String getErrorMessageFormat();

    public Map<String, Object> getParams();
}