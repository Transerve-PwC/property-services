package org.egov.cpt.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DuplicateCopyResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("DuplicateCopyApplications")
	@Valid
	private List<DuplicateCopy> duplicateCopyApplications;

	public DuplicateCopyResponse addPropertiesItem(DuplicateCopy applicationItem) {
		if (this.duplicateCopyApplications == null) {
			this.duplicateCopyApplications = new ArrayList<>();
		}
		this.duplicateCopyApplications.add(applicationItem);
		return this;
	}

}
