package org.egov.cpt.models;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Owner {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("ownerId")
	private String ownerId;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("dateOfBirth")
	private String dateOfBirth;

	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@JsonProperty("allotmentStartdate")
	private String allotmentStartdate;

	@JsonProperty("allotmentEnddate")
	private String allotmentEnddate;

	@JsonProperty("posessionStartdate")
	private String posessionStartdate;

	@JsonProperty("posessionEnddate")
	private String posessionEnddate;

	@JsonProperty("allotmenNumber")
	private String allotmenNumber;

	@JsonProperty("applicationStatus")
	private String applicationStatus;

	@JsonProperty("activeState")
	private Boolean activeState;

	@JsonProperty("isPrimaryOwner")
	private String isPrimaryOwner;

	@JsonProperty("monthlyRent")
	private String monthlyRent;

	@JsonProperty("revisionPeriod")
	private String revisionPeriod;

	@JsonProperty("revisionPercentage")
	private String revisionPercentage;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@Valid
	@JsonProperty("payment")
	private List<Payment> payment;

//	@Builder
//	public Owner(String id, String property_id, String owner_id, String name, String email, String phone, String gender,
//			String date_of_birth, String aadhaar_number, String allotment_startdate, String allotment_enddate,
//			String posession_startdate, String posession_enddate, String allotmen_number, String application_status,
//			Boolean active_state, String is_primary_owner, String monthly_rent, String revision_period,
//			String revision_percentage, AuditDetails auditDetails) {
//
//		this.id = id;
//		this.propertyId = property_id;
//		this.ownerId = owner_id;
//		this.name = name;
//		this.email = email;
//		this.phone = phone;
//		this.gender = gender;
//		this.dateOfBirth = date_of_birth;
//		this.aadhaarNumber = aadhaar_number;
//		this.allotmentStartdate = allotment_startdate;
//		this.allotmentEnddate = allotment_enddate;
//		this.posessionStartdate = posession_startdate;
//		this.posessionEnddate = posession_enddate;
//		this.allotmenNumber = allotmen_number;
//		this.applicationStatus = application_status;
//		this.activeState = active_state;
//		this.isPrimaryOwner = is_primary_owner;
//		this.monthlyRent = monthly_rent;
//		this.revisionPeriod = revision_period;
//		this.revisionPercentage = revision_percentage;
//		this.auditDetails = auditDetails;
//
//	}

}
