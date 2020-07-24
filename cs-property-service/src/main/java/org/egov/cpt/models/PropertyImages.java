package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertySerializer;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Property
 */
@ApiModel(description = "A Object holds the basic data for a Notice Generation")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PropertyImages {

	@JsonProperty("id")
	private String id;

//	@JsonSerialize(using = PropertySerializer.class)
	@JsonProperty("property")
	private Property property;

	@JsonProperty("tenantId")
	private String tenantId;
	

	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("description")
	private String description;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@Valid
	@JsonProperty("applicationDocuments")
	private List<PropertyImagesDocument> applicationDocuments = null;


	public PropertyImages addApplicationDocumentsItem(PropertyImagesDocument applicationDocumentsItem) {
		if (this.applicationDocuments == null) {
			this.applicationDocuments = new ArrayList<>();
		}
		if (!this.applicationDocuments.contains(applicationDocumentsItem))
			this.applicationDocuments.add(applicationDocumentsItem);
		return this;
	}

}
