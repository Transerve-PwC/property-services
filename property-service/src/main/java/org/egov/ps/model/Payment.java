package org.egov.ps.model;

import java.math.BigDecimal;

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
 * A Object holds the basic data for a Payment
 */
@ApiModel(description = "A Object holds the basic data for a Payment")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-08-10T13:06:11.263+05:30")

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("ownerDetailsId")
	private String ownerDetailsId;

//	Ground Rent

	@JsonProperty("grDueDateOfPayment")
	private Long grDueDateOfPayment;

	@JsonProperty("grPayable")
	private BigDecimal grPayable;

	@JsonProperty("grAmountOfGr")
	private BigDecimal grAmountOfGr;

	@JsonProperty("grTotalGr")
	private BigDecimal grTotalGr;

	@JsonProperty("grDateOfDeposit")
	private Long grDateOfDeposit;

	@JsonProperty("grDelayInPayment")
	private BigDecimal grDelayInPayment;

	@JsonProperty("grInterestForDelay")
	private BigDecimal grInterestForDelay;

	@JsonProperty("grTotalAmountDueWithInterest")
	private BigDecimal grTotalAmountDueWithInterest;

	@JsonProperty("grAmountDepositedGr")
	private BigDecimal grAmountDepositedGr;

	@JsonProperty("grAmountDepositedIntt")
	private BigDecimal grAmountDepositedIntt;

	@JsonProperty("grBalanceGr")
	private BigDecimal grBalanceGr;

	@JsonProperty("grBalanceIntt")
	private BigDecimal grBalanceIntt;

	@JsonProperty("grTotalDue")
	private BigDecimal grTotalDue;

	@JsonProperty("grReceiptNumber")
	private String grReceiptNumber;

	@JsonProperty("grReceiptDate")
	private Long grReceiptDate;

//	Service Tax/GST												

	@JsonProperty("stRateOfStGst")
	private BigDecimal stRateOfStGst;

	@JsonProperty("stAmountOfGst")
	private BigDecimal stAmountOfGst;

	@JsonProperty("stAmountDue")
	private BigDecimal stAmountDue;

	@JsonProperty("stDateOfDeposit")
	private Long stDateOfDeposit;

	@JsonProperty("stDelayInPayment")
	private BigDecimal stDelayInPayment;

	@JsonProperty("stInterestForDelay")
	private BigDecimal stInterestForDelay;

	@JsonProperty("stTotalAmountDueWithInterest")
	private BigDecimal stTotalAmountDueWithInterest;

	@JsonProperty("stAmountDepositedStGst")
	private BigDecimal stAmountDepositedStGst;

	@JsonProperty("stAmountDepositedIntt")
	private BigDecimal stAmountDepositedIntt;

	@JsonProperty("stBalanceStGst")
	private BigDecimal stBalanceStGst;

	@JsonProperty("stBalanceIntt")
	private BigDecimal stBalanceIntt;

	@JsonProperty("stTotalDue")
	private BigDecimal stTotalDue;

	@JsonProperty("stReceiptNumber")
	private String stReceiptNumber;

	@JsonProperty("stReceiptDate")
	private Long stReceiptDate;

	@JsonProperty("stPaymentMadeBy")
	private String stPaymentMadeBy;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
