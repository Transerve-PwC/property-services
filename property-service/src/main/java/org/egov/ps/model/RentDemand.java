package org.egov.ps.model;

import java.util.Date;

import org.egov.ps.model.PurchaseDetails.PurchaseDetailsBuilder;
import org.egov.ps.web.contracts.AuditDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RentDemand {
	
	/**
	 * Unique id of the demand
	 */
	private String id;
	
	/**
	 * Property that this rent is generated for.
	 */
	private String propertyId;
	/**
	 * No of days of grace period before interest starts getting applied.
	 */
	@Builder.Default
	private int initialGracePeriod = 10;
    
	/**
	 * Date of generation of this demand.
	 */
	private Date generationDate;
    
	/**
	 * The principal rent amount that is to be collected
	 */
    private Double collectionPrincipal;
    
    /**
     * The remaining principal that still has to be collected.
     */
    @Builder.Default
    private Double remainingPrincipal = 0.0;
    
    /**
     * Last date on which interest was made as 0.
     */
    private Date interestSince;
    
    /**
     * The remaining interest that still has to be collected.
     */
    private Double remainingInterest;
}
