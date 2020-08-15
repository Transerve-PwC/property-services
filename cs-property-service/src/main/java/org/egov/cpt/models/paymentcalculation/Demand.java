package org.egov.cpt.models.paymentcalculation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Demand {

	 @JsonAlias({"Month"})
	 @JsonProperty("generationDate")
	 private String generationDate;
	 
	 @JsonAlias({"Assessment Amount"})
	 @JsonProperty("collectionPrincipal")
	 private String collectionPrincipal;
	
}
