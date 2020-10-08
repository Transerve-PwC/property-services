package org.egov.ps.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.ps.web.contracts.AuditDetails;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@Builder
public class AuctionBidder {

	@JsonProperty("id")
	private String id;

	/**
	 * Initial Excel parse in Property Master / Allotment
	 */
	@JsonProperty("auctionId")
	private String auctionId;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("description")
	private String description;

	/**
	 * Initial Excel parse in Property Master / Allotment
	 */
	@JsonProperty("bidderName")
	private String bidderName;

	/**
	 * Initial Excel parse in Property Master / Allotment
	 */
	@JsonProperty("depositedEMDAmount")
	private BigDecimal depositedEMDAmount;

	/**
	 * Initial Excel parse in Property Master / Allotment
	 */
	@JsonProperty("depositDate")
	private Long depositDate;

	/**
	 * Initial Excel parse in Property Master / Allotment
	 */
	@JsonProperty("emdValidityDate")
	private Long emdValidityDate;

	/**
	 * Initial Excel parse in Property Master / Allotment Also updated as part of
	 * Refund Flow.
	 */
	@JsonProperty("refundStatus")
	private String refundStatus;

	/**
	 * Updated as part of workflow.
	 */
	private String state;

	/**
	 * Updated as part of workflow.
	 */
	private String action;

	/**
	 * Updated as part of workflow.
	 */
	private String comments;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
}