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

	@JsonProperty("Properties")
	@Valid
	private List<DuplicateCopy> properties;

	public DuplicateCopyResponse addPropertiesItem(DuplicateCopy propertiesItem) {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		this.properties.add(propertiesItem);
		return this;
	}

}
