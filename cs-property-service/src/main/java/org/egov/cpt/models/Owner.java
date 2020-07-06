package org.egov.cpt.models;

import org.egov.cpt.util.CustomListSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
public class Owner {

	@JsonProperty("id")
	private String id;

	@JsonSerialize(using = CustomListSerializer.class)
	private Property property;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("allotmenNumber")
	private String allotmenNumber;

	@JsonProperty("activeState")
	private Boolean activeState;

	@JsonProperty("isPrimaryOwner")
	private String isPrimaryOwner;

	@JsonProperty("ownerDetails")
	private OwnerDetails ownerDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("applicationState")
	private String applicationState;

	@JsonProperty("applicationAction")
	private String applicationAction;

}
