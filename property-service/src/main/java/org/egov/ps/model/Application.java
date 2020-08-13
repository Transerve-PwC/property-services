package org.egov.ps.model;

import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the basic data for a Application
 */
@ApiModel(description = "A Object holds the basic data for a Application")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-08-12T10:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Application {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("applicationNumber")
	private String applicationNumber;

	@JsonProperty("branchType")
	private String branchType;

	@JsonProperty("moduleType")
	private String moduleType;

	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("comments")
	private String comments;

	@JsonProperty("hardcopyReceivedDate")
	private Long hardcopyReceivedDate;

	@JsonProperty("additionalDetails")
	private JsonNode additionalDetails;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	@JsonProperty("applicant")
	private Applicant applicant;
}
