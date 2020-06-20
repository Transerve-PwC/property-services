package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Payment {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("amountDue")
	private String amountDue;

	@JsonProperty("amountPaid")
	private String amountPaid;

	@JsonProperty("receiptNumber")
	private String receiptNumber;

	@JsonProperty("paymentDate")
	private String paymentDate;

	@JsonProperty("paymentMode")
	private String paymentMode;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
}
