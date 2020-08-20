package org.egov.ps.model;

import java.util.List;

import org.egov.ps.web.contracts.State;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkFlowDetails {

	public String tenantId;
	public String businessService;
	public String business;
	public Long businessServiceSla;
	public List<State> states;
}
