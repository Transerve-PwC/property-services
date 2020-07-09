package org.egov.cpt.models.calculation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.cpt.models.Owner;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Calculation
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-27T14:56:03.454+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {

	@JsonProperty("applicationNumber")
	private String applicationNumber = null;

	@JsonProperty("owner")
	private Owner owner = null;

	@NotNull
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;

//	@JsonProperty("taxHeadEstimates")
//	List<TaxHeadEstimate> taxHeadEstimates;
//
//	@JsonProperty("tradeTypeBillingIds")
//	FeeAndBillingSlabIds tradeTypeBillingIds;
//
//	@JsonProperty("accessoryBillingIds")
//	FeeAndBillingSlabIds accessoryBillingIds;

}
