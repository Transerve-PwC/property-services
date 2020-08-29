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
public class RentAccountStatement {
    /*
     * Demand/Payment generation date
     */
    private long date;
    /*
     * Demand/Payment amount
     */
    private double amount;

    private String type;
    private double remainingPrincipal;
    private double remainingInterest;
    private double dueAmount;

    public enum Type {
        C("C"), D("D");

        private String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static String fromValue(String text) {
            for (Type b : Type.values()) {
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b.value;
                }
            }
            return null;
        }
    }
}