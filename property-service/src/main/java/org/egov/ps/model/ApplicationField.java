package org.egov.ps.model;

import java.util.ArrayList;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ApplicationField implements IApplicationField {

    private String path;

    private boolean required;

    private Map<String, Object> rootObject;

    private ArrayList<IValidation> validations;

    private Object value;
}