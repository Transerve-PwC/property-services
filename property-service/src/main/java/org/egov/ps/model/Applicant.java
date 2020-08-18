package org.egov.ps.model;

import org.egov.ps.util.PropertySerializer;
import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the basic data for a Applicant
 */
@ApiModel(description = "A Object holds the basic data for a Applicant")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-08-12T10:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Applicant {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;
	
	@JsonSerialize(using = PropertySerializer.class)
	private Property property;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("applicantDetails")
    private JsonNode applicantDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
