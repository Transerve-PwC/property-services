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
	
	@JsonProperty("decreeDate")
	private Long decreeDate;
	
	@JsonProperty("courtDetails")
	private String courtDetails;
	
	@JsonProperty("civilTitledAs")
	private String civilTitledAs;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("owners")
	private List<Owner> owners;
	
	@JsonProperty("companyRegistrationDate")
	private Long companyRegistrationDate;
	
	@JsonProperty("companyOrFirm")
	private String companyOrFirm;
	
	@JsonProperty("propertyRegisteredTo")
	private String propertyRegisteredTo;
	
	@JsonProperty("entityType")
	private String entityType;

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
	@JsonProperty("courtCases")
	private List<CourtCase> courtCases;

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

	@JsonProperty("paymentDetails")
	private List<Payment> paymentDetails;

	public PropertyDetails addPaymentItem(Payment paymentItem) {
		if (this.paymentDetails == null) {
			this.paymentDetails = new ArrayList<>();
		}
		for (Payment paymentDetail : paymentDetails) {
			if (paymentDetail.getId().equalsIgnoreCase(paymentItem.getId())) {
				return this;
			}
		}
		this.paymentDetails.add(paymentItem);
		return this;
	}

}
