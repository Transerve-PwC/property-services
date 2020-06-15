package org.egov.cpt.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class PropertyDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("transitNumber")
	private String transitNumber;

	@JsonProperty("area")
	private String area;

	@JsonProperty("rentPerSqyd")
	private String rentPerSqyd;

	@JsonProperty("currentOwner")
	private String currentOwner;

	@JsonProperty("floors")
	private String floors;

	@JsonProperty("additionalDetails")
	private String additionalDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

//	@Builder
//	public PropertyDetails(String id, String propertyId, String transitNumber, String area, String rentPerSqyd,
//			String currentOwner, String floors, String additionalDetails) {
//		this.id = id;
//		this.propertyId = propertyId;
//		this.transitNumber = transitNumber;
//		this.area = area;
//		this.rentPerSqyd = rentPerSqyd;
//		this.currentOwner = currentOwner;
//		this.floors = floors;
//		this.additionalDetails = additionalDetails;
//	}
}
