package org.egov.ps.model;

import org.egov.ps.web.contracts.AuditDetails;

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
public class AuctionMaster {

	private String id;
	
	private String fileNumber;	
	
	private String estAuctionIdLabel;	
	
	private Long dateOfAuction;
	
	private String modeOfAuction;
		
	private String estEstateSchemeNameLabel;		
	
	private String estEMDAmountLabel;
	
	private String estEMDAmountDateLabel;
	
	@JsonIgnore
	private AuditDetails auditDetails;
}
