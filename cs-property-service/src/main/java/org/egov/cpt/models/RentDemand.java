package org.egov.cpt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RentDemand implements Comparable<RentDemand> {

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

  @JsonProperty("mode")
  @Builder.Default
  private ModeEnum mode = ModeEnum.UPLOAD;

  @JsonProperty("status")
  @Builder.Default
  private PaymentStatusEnum status = PaymentStatusEnum.UNPAID;

  @JsonProperty("auditDetails")
  @Builder.Default
  private AuditDetails auditDetails = null;

  @Override
  public int compareTo(RentDemand other) {
    return this.getGenerationDate().compareTo(other.getGenerationDate());
  }

  public boolean isPaid() {
    return this.status == PaymentStatusEnum.PAID;
  }

  public boolean isUnPaid() {
    return !this.isPaid();
  }

  public void setRemainingPrincipalAndUpdatePaymentStatus(Double d) {
    this.setRemainingPrincipal(d);
    if (this.remainingPrincipal == 0) {
      this.status = PaymentStatusEnum.PAID;
    } else {
      this.status = PaymentStatusEnum.UNPAID;
    }
  }
}
