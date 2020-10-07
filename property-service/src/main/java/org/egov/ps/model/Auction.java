package org.egov.ps.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.ps.web.contracts.AuditDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Data
@Builder
@NoArgsConstructor
public class Auction {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("auctionId")
	private String auctionId;
	
	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("tenantId")
	private String tenantId;
	
	@JsonProperty("fileNumber")
	private String fileNumber;
	
	@JsonProperty("auctionDescription")
	private String auctionDescription;	
	
	@JsonProperty("participatedBidders")
	private String participatedBidders;	
	
	@JsonProperty("depositedEMDAmount")
	private BigDecimal depositedEMDAmount;
	
	@JsonProperty("depositDate")
	private Long depositDate;
		
	@JsonProperty("emdValidityDate")
	private Long emdValidityDate;
	
	@JsonProperty("refundStatus")
	private String refundStatus;
	
	@JsonProperty("comment")
	private String comment;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	

}