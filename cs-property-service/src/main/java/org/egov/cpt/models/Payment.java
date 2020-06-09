package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Payment {

	@JsonProperty("amount_due")
	private String amount_due;

	@JsonProperty("amount_paid")
	private String amount_paid;

	@JsonProperty("receipt_number")
	private String receipt_number;

	@JsonProperty("payment_date")
	private String payment_date;

	@JsonProperty("payment_mode")
	private String payment_mode;
}
