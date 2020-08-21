package org.egov.ps.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WorkFlowResponseDetails {

	private String workFlowName;
	private boolean created;
	private String message;
}