package org.egov.cpt.web.contracts;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.cpt.models.RentAccountStatement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountStatementResponse {

	@JsonProperty("RentAccountStatements")
	@Valid
	private List<RentAccountStatement> rentAccountStatements;

}
