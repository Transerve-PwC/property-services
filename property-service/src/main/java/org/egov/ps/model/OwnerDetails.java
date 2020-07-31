package org.egov.ps.model;

import org.egov.ps.web.contracts.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("ownerId")
	private String ownerId;

	@JsonProperty("ownerName")
	private String ownerName;

	@JsonProperty("guardianName")
	private String guardianName;

	@JsonProperty("guardianRelation")
	private String guardianRelation;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("allotmentNumber")
	private String allotmentNumber;

	@JsonProperty("dateOfAllotment")
	private Long dateOfAllotment;

	@JsonProperty("possesionDate")
	private Long possesionDate;

	@JsonProperty("isCurrentOwner")
	private Boolean isCurrentOwner;

	@JsonProperty("isMasterEntry")
	private Boolean isMasterEntry;

	@JsonProperty("dueAmount")
	private String dueAmount;

	@JsonProperty("address")
	private String address;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
