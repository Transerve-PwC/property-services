package org.egov.ps.model;

import java.util.Date;

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
public class RentPayment {
	/**
	 * Unique id of the demand
	 */
	private String id;

	/**
	 * Amount payed by the renter
	 */
	private Double amountPaid;
	
	/**
	 * Receipt no of the payment
	 */
	private String receiptNo;

	/**
	 * Property for which the rent is paid for.
	 */
	private String propertyId;
	
	/**
	 * Date of payment
	 */
	private Date dateOfPayment;

}
