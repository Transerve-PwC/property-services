package org.egov.ps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	private Integer id;
	
	private String auctionDescription;	
	
	private String participatedBidders;	
	
	private Long depositedEMDAmount;
	
	private String depositDate;
		
	private String emdValidityDate;		
	
	private String refundStatus;
	
	@JsonIgnore
	private Long createdBy;

	@JsonIgnore
    private Long lastModifiedBy;

}