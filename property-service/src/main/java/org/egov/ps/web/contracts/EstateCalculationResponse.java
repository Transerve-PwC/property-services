package org.egov.ps.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.model.EstateCalculationModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstateCalculationResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;
	
	@JsonProperty("Calculations")
	@Valid
	private List<EstateCalculationModel> estateCalculationModels;
	
	public EstateCalculationResponse addEstateCalculations(EstateCalculationModel estateCalculationModel) {
		if (this.estateCalculationModels == null) {
			this.estateCalculationModels = new ArrayList<>();
		}
		this.estateCalculationModels.add(estateCalculationModel);
		return this;
	}
}
