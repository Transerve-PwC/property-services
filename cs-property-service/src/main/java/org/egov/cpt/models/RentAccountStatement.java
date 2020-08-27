package org.egov.cpt.models;
import java.sql.Date;

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

public class RentAccountStatement {
	private long date;
	private double amount;
	private String type;
	private double remainingPrincipal;
	private double remainingInterest;
	private double dueAmount;
	
	
	

}
