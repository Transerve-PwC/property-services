package org.egov.ps.model;

import java.util.ArrayList;
import java.util.List;

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
 * A Object holds the basic data for a Court Case
 */
@ApiModel(description = "A Object holds the basic data for a Court Case")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-31T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PurchaseDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("propertyDetailsId")
	private String propertyDetailsId;

	@JsonProperty("newOwnerName")
	private String newOwnerName;

	@JsonProperty("newOwnerFatherName")
	private String newOwnerFatherName;

	@JsonProperty("newOwnerAddress")
	private String newOwnerAddress;

	@JsonProperty("newOwnerMobileNumber")
	private String newOwnerMobileNumber;

	@JsonProperty("sellerName")
	private String sellerName;

	@JsonProperty("sellerFatherName")
	private String sellerFatherName;

	@JsonProperty("percentageOfShare")
	private String percentageOfShare;

	@JsonProperty("modeOfTransfer")
	private String modeOfTransfer;

	@JsonProperty("registrationNumber")
	private String registrationNumber;

	@JsonProperty("dateOfRegistration")
	private Long dateOfRegistration;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	@JsonProperty("purchaserDocuments")
	private List<Document> purchaserDocuments;

	public PurchaseDetails addPurchaserDocumentsItem(Document purchaserDocumentItem) {
		if (this.purchaserDocuments == null) {
			this.purchaserDocuments = new ArrayList<>();
		}
		for (Document purchaserDocument : purchaserDocuments) {
			if (purchaserDocument.getId().equalsIgnoreCase(purchaserDocumentItem.getId())) {
				return this;
			}
		}
		this.purchaserDocuments.add(purchaserDocumentItem);
		return this;
	}

}
