package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.cpt.models.calculation.Calculation;
import org.springframework.validation.annotation.Validated;

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
public class RentDetail {

	@JsonProperty("transitNumber")
	private String transitNumber;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("amount")
	private Double amount;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("calculation")
	Calculation calculation;

}