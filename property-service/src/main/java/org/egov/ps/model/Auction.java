package org.egov.ps.model;

import java.math.BigInteger;

import org.egov.ps.web.contracts.AuditDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private Double depositedEMDAmount;
	
	@JsonProperty("depositDate")
	private BigInteger depositDate;
		
	@JsonProperty("emdValidityDate")
	private BigInteger emdValidityDate;
	
	@JsonProperty("refundStatus")
	private String refundStatus;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}