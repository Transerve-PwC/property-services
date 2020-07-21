package org.egov.cpt.models;

import java.math.BigDecimal;

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
public class MortgageApprovedGrantDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyDetailId")
	private String propertyDetailId;

	@JsonProperty("ownerId")
	private String ownerId;

	@JsonProperty("tenentId")
	private String tenentId;

	@JsonProperty("bankName")
	private String bankName;

	@JsonProperty("mortgageAmount")
	private BigDecimal mortgageAmount;

	@JsonProperty("sanctionLetterNumber")
	private String sanctionLetterNumber;

	@JsonProperty("sanctionDate")
	private Long sanctionDate;

	@JsonProperty("mortgageEndDate")
	private Long mortgageEndDate;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

}
