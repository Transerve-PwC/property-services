package org.egov.cpt.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.PropertyImages;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyImagesResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("PropertyImagesApplications")
	@Valid
	private List<PropertyImages> propertyImagesApplications;

	public PropertyImagesResponse addPropertiesItem(PropertyImages applicationItem) {
		if (this.propertyImagesApplications == null) {
			this.propertyImagesApplications = new ArrayList<>();
		}
		this.propertyImagesApplications.add(applicationItem);
		return this;
	}

}
