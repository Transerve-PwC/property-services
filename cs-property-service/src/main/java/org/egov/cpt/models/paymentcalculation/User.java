package org.egov.cpt.models.paymentcalculation;

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
public class User {
	
	
	private String id;
	
	private Double remainingAmount;
	

}
