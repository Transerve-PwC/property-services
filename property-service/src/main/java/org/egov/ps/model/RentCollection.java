package org.egov.ps.model;

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
public class RentCollection {
	/**
	 * Unique id of the collection
	 */
	private String id;

	/**
	 * The paymentId of the payment that this is a part of.
	 */
	private String paymentId;
	
	
	/**
	 * Demand Id of the demand that this fulfils.
	 */
	private String demandId;
	
	@Builder.Default
	private Double interestCollected = 0.0;
	
	@Builder.Default
	private Double principalCollected = 0.0;
	
}
