package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

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
public class Property {

	@JsonProperty("id")
	private String id;

	@JsonProperty("transitNumber")
	private String transitNumber;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("colony")
	private String colony;

	@JsonProperty("masterDataState")
	private String masterDataState;

	@JsonProperty("masterDataAction")
	private String masterDataAction;

	@JsonProperty("assignee")
	private List<String> assignee = null;

	@Size(max = 128)
	@JsonProperty("comment")
	private String comment;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("propertyDetails")
	private PropertyDetails propertyDetails;

	@Valid
	@JsonProperty("owners")
	private List<Owner> owners;

	@Valid
	@JsonProperty
	private List<DuplicateCopy> duplicateCopys;

	public Property addDocumentItem(DuplicateCopy newDuplicateCopyItem) {
		if (this.duplicateCopys == null) {
			this.duplicateCopys = new ArrayList<>();
		}
		for (DuplicateCopy duplicateCopy : duplicateCopys) {
			if (duplicateCopy.getId().equalsIgnoreCase(newDuplicateCopyItem.getId())) {
				return this;
			}
		}
		this.duplicateCopys.add(newDuplicateCopyItem);
		return this;
	}

	public Property addOwnerItem(Owner newOwnerItem) {
		if (this.owners == null) {
			this.owners = new ArrayList<>();
		}
		for (Owner owner : owners) {
			if (owner.getId().equalsIgnoreCase(newOwnerItem.getId())) {
				return this;
			}
		}
		this.owners.add(newOwnerItem);
		return this;
	}

	@JsonProperty("pincode")
	private String pincode;

}
