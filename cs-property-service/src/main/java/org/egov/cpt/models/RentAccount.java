package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RentAccount {
	
	
	private String id;
	
	private String propertyId;
	
	private Double remainingAmount;
	
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	

}
