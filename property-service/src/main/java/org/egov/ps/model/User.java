package org.egov.ps.model;

import java.util.Date;

import org.egov.ps.model.RentDemand.RentDemandBuilder;

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
