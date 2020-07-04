package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Owner {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("allotmenNumber")
	private String allotmenNumber;

	@JsonProperty("applicationStatus")
	private String applicationStatus; // TODO is this same as masterDataState in property

	@JsonProperty("activeState")
	private Boolean activeState;

	@JsonProperty("isPrimaryOwner")
	private String isPrimaryOwner;

	@JsonProperty("ownerDetails")
	private OwnerDetails ownerDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

}
