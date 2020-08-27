package org.egov.cpt.web.contracts;

import java.util.List;

import javax.validation.Valid;

import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountStatementResponse {

	@JsonProperty("Payments")
	@Valid
	private List<RentPayment> payments;	
	
	@JsonProperty("Demands")
	@Valid
	private List<RentDemand> demands;
	
	@JsonProperty("Collections")
	@Valid
	private List<RentCollection> collections;
}
