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
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-31T17:06:11.263+05:30")

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

	/**
	 * One of the categories from `data/ch/EstateProperties/Categories.json`
	 * CAT.RESIDENTIAL,
	 * CAT.COMMERCIAL,
	 * CAT.INDUSTRIAL,
	 * CAT.INSTITUTIONAL,
	 * CAT.GOVPROPERTY,
	 * CAT.RELIGIOUS,
	 * CAT.HOSPITAL,
	 * 
	 */
	@JsonProperty("category")
	private String category;

	@JsonProperty("subCategory")
	private String subCategory;

	@JsonProperty("siteNumber")
	private String siteNumber;

	@JsonProperty("sectorNumber")
	private String sectorNumber;

	@JsonProperty("propertyMasterOrAllotmentOfSite")
	private String propertyMasterOrAllotmentOfSite;

	@JsonProperty("isCancelationOfSite")
	private Boolean isCancelationOfSite;
	
	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

	@JsonProperty("propertyDetails")
	private PropertyDetails propertyDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
