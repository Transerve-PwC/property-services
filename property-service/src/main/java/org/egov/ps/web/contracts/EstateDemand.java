package org.egov.ps.web.contracts;


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
public class EstateDemand {

  /**
   * Unique id of the demand
   */
  @JsonProperty("id")
  private String id;
  
  /**
   * Date of demand.
   */
  @JsonProperty("demandDate")
  private Long demandDate;
  
  @JsonProperty("isPrevious")
  private Boolean isPrevious;

  /**
   * Rent of demand.
   */
  @JsonProperty("rent")
  private Double rent;
  
  /**
   * Penalty Interest of demand.
   */
  @JsonProperty("penaltyInterest")
  private Double penaltyInterest;
  
  /**
   * Gst Interest of demand.
   */
  @JsonProperty("gstInterest")
  private Double gstInterest;
  
  /**
   * GST of demand.
   */
  @JsonProperty("gst")
  private Double gst;
  
  /**
   * Collected Rent of demand.
   */
  @JsonProperty("collectedRent")
  private Double collectedRent;
  
  /**
   * Collected GST of demand.
   */
  @JsonProperty("collectedGST")
  private Double collectedGST;
  
  /**
   * Collected Interest Penalty of demand.
   */
  @JsonProperty("collectedInterestPenalty")
  private Double collectedInterestPenalty;
  
  /**
   * Collected GST Penalty of demand.
   */
  @JsonProperty("collectedGSTPenalty")
  private Double collectedGSTPenalty;
  
  
  

}
