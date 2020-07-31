package org.egov.ps.model;

import org.egov.ps.web.contracts.AuditDetails;
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
public class Property {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("fileNumber")
	private String fileNumber;

	@JsonProperty("category")
	private String category;

	@JsonProperty("subCategory")
	private String subCategory;

	@JsonProperty("siteNumber")
	private String siteNumber;

	@JsonProperty("sectorNumber")
	private String sectorNumber;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

	@JsonProperty("propertyDetails")
	private PropertyDetails propertyDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
