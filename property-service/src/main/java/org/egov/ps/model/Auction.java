package org.egov.ps.model;

import lombok.AllArgsConstructor;
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
@NoArgsConstructor
public class Auction {
	
	private Integer id;
	
	private String auctionDescription;	
	
	private String participatedBidders;	
	
	private Long depositedEMDAmount;
	
	private String depositDate;
		
	private String emdValidityDate;		
	
	private String refundStatus;

}