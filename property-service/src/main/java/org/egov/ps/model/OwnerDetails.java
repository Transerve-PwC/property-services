package org.egov.ps.model;

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
 * A Object holds the basic data for a Owner Details
 */
@ApiModel(description = "A Object holds the basic data for a Owner Details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-31T17:06:11.263+05:30")

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

	@JsonProperty("ownerDocuments")
	private List<OwnerDocument> ownerDocuments;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}