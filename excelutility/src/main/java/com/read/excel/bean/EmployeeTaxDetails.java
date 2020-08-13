package com.read.excel.bean;

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
public class EmployeeTaxDetails {

	@JsonProperty("Month")
    private String month;
	
	@JsonProperty("Assessment Amount")
    private String assessmentAmount;
	
	@JsonProperty("Realization Amount")
    private String realizationAmount;
	
	@JsonProperty("Balance Amount")
    private String balanceAmount;
	
	@JsonProperty("Total Days")
    private String totalDays;
	
	@JsonProperty("Interest @ 24% p.a.")
    private String interestDays;
	
	@JsonProperty("Days")
    private String days;
	
	@JsonProperty("Per day Interest Calculation")
    private String perDayInterestCalculation;
		
	@JsonProperty("Receipt No")
    private String receiptNo;
	
	@JsonProperty("Receipt Date")
    private String receiptDate;
	
}
