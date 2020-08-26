package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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
	@JsonProperty("id")
	private String id;

	/**
	 * Amount payed by the renter
	 */
	@JsonProperty("amountPaid")
	private Double amountPaid;
	
	/**
	 * Receipt no of the payment
	 */
	@JsonProperty("receiptNo")
	private String receiptNo;

	/**
	 * Property for which the rent is paid for.
	 */
	@JsonProperty("propertyId")
	private String propertyId;
	
	/**
	 * Date of payment
	 */
	@JsonProperty("dateOfPayment")
	private Long dateOfPayment;
	
	 public enum ModeEnum {
	        UPLOAD("Uploaded"),
	        
	        GENERATED("Generated");

	        private String value;

	        ModeEnum(String value) {
	          this.value = value;
	        }

	        @Override
	        @JsonValue
	        public String toString() {
	          return String.valueOf(value);
	        }

	        @JsonCreator
	        public static ModeEnum fromValue(String text) {
	          for (ModeEnum b : ModeEnum.values()) {
	            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
	              return b;
	            }
	          }
	          return null;
	        }
	      }
	    
	    @JsonProperty("mode")
	    private ModeEnum mode = null;
	    
	    @JsonProperty("auditDetails")
		private AuditDetails auditDetails = null;
	    
	    @JsonProperty("tenantId")
	    private String tenantId;
	    
	    @JsonProperty("active")
	    private String active;
	    
	    /**
	     * boolean indicates whether payment is proceed or not
	     */
	    private boolean proceed;

}
