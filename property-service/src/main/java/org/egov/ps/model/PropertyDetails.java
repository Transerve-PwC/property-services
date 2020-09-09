package org.egov.ps.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
 * A Object holds the basic data for a Property Details
 */
@ApiModel(description = "A Object holds the basic data for a Property Details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-31T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PropertyDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("branchType")
	private String branchType;

	@JsonProperty("propertyType")
	private String propertyType;

	@JsonProperty("typeOfAllocation")
	private String typeOfAllocation;

	@JsonProperty("emdAmount")
	private BigDecimal emdAmount;
	
	@JsonProperty("emdDate")
	private Long emdDate;

	@JsonProperty("modeOfAuction")
	private String modeOfAuction;

	@JsonProperty("schemeName")
	private String schemeName;

	@JsonProperty("dateOfAuction")
	private Long dateOfAuction;

	@JsonProperty("areaSqft")
	private int areaSqft;

	@JsonProperty("ratePerSqft")
	private BigDecimal ratePerSqft;

	@JsonProperty("lastNocDate")
	private Long lastNocDate;

	@JsonProperty("serviceCategory")
	private String serviceCategory;

	@JsonProperty("isPropertyActive")
	private Boolean isPropertyActive;

	@JsonProperty("tradeType")
	private String tradeType;

	@JsonProperty("companyName")
	private String companyName;

	@JsonProperty("companyAddress")
	private String companyAddress;

	@JsonProperty("companyRegistrationNumber")
	private String companyRegistrationNumber;

	@JsonProperty("companyType")
	private String companyType;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("owners")
	private List<Owner> owners;

	public PropertyDetails addOwnerItem(Owner newOwnerItem) {
		if (this.owners == null) {
			this.owners = new ArrayList<>();
		}
		for (Owner owner : owners) {
			if (owner.getId().equalsIgnoreCase(newOwnerItem.getId())) {
				return this;
			}
		}
		this.owners.add(newOwnerItem);
		return this;

	}

}
