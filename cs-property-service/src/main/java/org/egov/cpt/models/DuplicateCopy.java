package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@ApiModel(description = "A Object holds the basic data for a Property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DuplicateCopy {

	@JsonProperty("id")
	private String id;
	
	@Valid
	@JsonProperty("property")
	private Property property;
	
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@Valid
	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
	@Valid
	@JsonProperty("applicationDocuments")
	private List<DuplicateCopyDocument> applicationDocuments=null;

	@Valid
	@JsonProperty("applicant")
	private List<Applicant> applicant;
	
	public DuplicateCopy addApplicationDocumentsItem(DuplicateCopyDocument applicationDocumentsItem) {
	    if (this.applicationDocuments == null) {
	    this.applicationDocuments = new ArrayList<>();
	    }
	    if(!this.applicationDocuments.contains(applicationDocumentsItem))
	        this.applicationDocuments.add(applicationDocumentsItem);
	    return this;
	}

	
}
