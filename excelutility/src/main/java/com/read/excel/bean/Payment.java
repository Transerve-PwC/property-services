package com.read.excel.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {

	@JsonAlias({ "Realization Amount" })
	@JsonProperty("amountPaid")
	private String amountPaid;

	@JsonAlias({ "Receipt No" })
	@JsonProperty("receiptNo")
	private String receiptNo;

	@JsonAlias({ "Receipt Date" })
	@JsonProperty("dateOfPayment")
	private String dateOfPayment;

}
