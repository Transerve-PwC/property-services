package org.egov.cpt.models;

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
	private CollectionAgainst collectionAgainst = CollectionAgainst.PAYMENT;
	
	/**
	 * Interest collected.
	 */
	@Builder.Default
	private Double interestCollected = 0.0;
	
	/**
	 * Principal collected.
	 */
	@Builder.Default
	private Double principalCollected = 0.0;
	
	public enum CollectionAgainst {
		PAYMENT("PAYMENT"),

		ACCOUNTBALANCE("ACCOUNTBALANCE");

		private String value;

		CollectionAgainst(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static CollectionAgainst fromValue(String text) {
			for (CollectionAgainst b : CollectionAgainst.values()) {
				if (String.valueOf(b.value).equalsIgnoreCase(text)) {
					return b;
				}
			}
			return null;
		}
	}
}
