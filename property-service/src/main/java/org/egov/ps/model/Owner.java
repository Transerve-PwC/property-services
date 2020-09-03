package org.egov.ps.model;

import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the basic data for a Owner
 */
@ApiModel(description = "A Object holds the basic data for a Owner")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-31T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Owner {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("propertyDetailsId")
	private String propertyDetailsId;

	@JsonProperty("serialNumber")
	private String serialNumber;

	@JsonProperty("share")
	private double share;

	@JsonProperty("cpNumber")
	private String cpNumber;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

	@JsonProperty("ownerDetails")
	private OwnerDetails ownerDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	@JsonProperty("mortgageDetails")
	private MortgageDetails mortgageDetails;

}
