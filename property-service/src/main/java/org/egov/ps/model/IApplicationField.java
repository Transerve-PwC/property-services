package org.egov.ps.model;

import java.util.ArrayList;
import java.util.Map;

public interface IApplicationField {

    public String getPath();

    public boolean isRequired();

    public Map<String, Object> getRootObject();

    public ArrayList<IValidation> getValidations();

    public Object getValue();
}
