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
public class RentDemand {
	
	/**
	 * Unique id of the demand
	 */
	@JsonProperty("id")
	private String id;
	
	/**
	 * Property that this rent is generated for.
	 */
	@JsonProperty("propertyId")
	private String propertyId;
	/**
	 * No of days of grace period before interest starts getting applied.
	 */
	@Builder.Default
	@JsonProperty("initialGracePeriod")
	private int initialGracePeriod = 10;
    
	/**
	 * Date of generation of this demand.
	 */
	@JsonProperty("generationDate")
	private Long generationDate;
    
	/**
	 * The principal rent amount that is to be collected
	 */
	@JsonProperty("collectionPrincipal")
    private Double collectionPrincipal;
    
    /**
     * The remaining principal that still has to be collected.
     */
    @Builder.Default
    @JsonProperty("remainingPrincipal")
    private Double remainingPrincipal = 0.0;
    
    /**
     * Last date on which interest was made as 0.
     */
    @JsonProperty("interestSince")
    private Long interestSince;
  
    
    @JsonProperty("tenantId")
    private String tenantId;
    
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
}
