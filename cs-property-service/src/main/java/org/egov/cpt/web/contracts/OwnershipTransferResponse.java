package org.egov.cpt.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Owner;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnershipTransferResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("Owners")
	@Valid
	private List<Owner> owners;

	public OwnershipTransferResponse addOwnersItem(Owner ownersItem) {
		if (this.owners == null) {
			this.owners = new ArrayList<>();
		}
		this.owners.add(ownersItem);
		return this;
	}

}
