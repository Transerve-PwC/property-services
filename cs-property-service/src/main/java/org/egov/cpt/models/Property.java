package org.egov.cpt.models;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Property
 */
@ApiModel(description = "A Object holds the basic data for a Property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Property {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("transitNumber")
	private String transitNumber;

	@JsonProperty("colony")
	private String colony;

	@JsonProperty("rentPerSqyd")
	private String rentPerSqyd;

	@JsonProperty("floors")
	private String floors;

	@JsonProperty("area")
	private String area;

	@JsonProperty("currentOwner")
	private String currentOwner;

	@JsonProperty("masterDataState")
	private String masterDataState;

	@JsonProperty("masterDataAction")
	private String masterDataAction;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@Valid
	@JsonProperty("owners")
	private List<Owner> owners;

	@JsonProperty("additionalDetails")
	private JsonNode additionalDetails;

}
