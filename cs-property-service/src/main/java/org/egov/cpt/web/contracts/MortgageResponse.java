package org.egov.cpt.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Mortgage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MortgageResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("MortgageApplications")
	@Valid
	private List<Mortgage> mortgageApplications;

	public MortgageResponse addPropertiesItem(Mortgage applicationItem) {
		if (this.mortgageApplications == null) {
			this.mortgageApplications = new ArrayList<>();
		}
		this.mortgageApplications.add(applicationItem);
		return this;
	}

}
