package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("id")
	private String id;

	/**
	 * The paymentId of the payment that this is a part of.
	 */
	@JsonProperty("paymentId")
	private String paymentId;
	
	
	/**
	 * Demand Id of the demand that this fulfils.
	 */
	@JsonProperty("demandId")
	private String demandId;
	
	
	@Builder.Default
	@JsonProperty("collectionAgainst")
	private CollectionAgainst collectionAgainst = CollectionAgainst.PAYMENT;
	
	/**
	 * Interest collected.
	 */
	@Builder.Default
	@JsonProperty("interestCollected")
	private Double interestCollected = 0.0;
	
	/**
	 * Principal collected.
	 */
	@Builder.Default
	@JsonProperty("principalCollected")
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

	 @JsonProperty("tenantId")
	 private String tenantId;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
}
