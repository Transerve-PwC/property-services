package org.egov.ps.model;

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

	@JsonProperty("propertyType")
	private String propertyType;

	@JsonProperty("typeOfAllocation")
	private String typeOfAllocation;

	@JsonProperty("modeOfAuction")
	private String modeOfAuction;

	@JsonProperty("schemeName")
	private String schemeName;

	@JsonProperty("dateOfAuction")
	private Long dateOfAuction;

	@JsonProperty("areaSqft")
	private String areaSqft;

	@JsonProperty("ratePerSqft")
	private String ratePerSqft;

	@JsonProperty("lastNocDate")
	private Long lastNocDate;

	@JsonProperty("serviceCategory")
	private String serviceCategory;

	@JsonProperty("owners")
	private List<Owner> owners;

	@JsonProperty("courtCases")
	private List<CourtCase> courtCases;

	@JsonProperty("purchaseDetails")
	private List<PurchaseDetails> purchaseDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

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

	public PropertyDetails addCourtCaseItem(CourtCase courtCaseItem) {
		if (this.courtCases == null) {
			this.courtCases = new ArrayList<>();
		}
		for (CourtCase courtCase : courtCases) {
			if (courtCase.getId().equalsIgnoreCase(courtCaseItem.getId())) {
				return this;
			}
		}
		this.courtCases.add(courtCaseItem);
		return this;
		
	}
	
	public PropertyDetails addPurchaseDetailsItem(PurchaseDetails purchaseDetailsItem) {
		if (this.purchaseDetails == null) {
			this.purchaseDetails = new ArrayList<>();
		}
		for (PurchaseDetails purchaseDetail : purchaseDetails) {
			if (purchaseDetail.getId().equalsIgnoreCase(purchaseDetailsItem.getId())) {
				return this;
			}
		}
		this.purchaseDetails.add(purchaseDetailsItem);
		return this;
		
	}

}
