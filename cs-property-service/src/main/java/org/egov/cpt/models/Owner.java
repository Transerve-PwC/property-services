package org.egov.cpt.models;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Owner {

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("date_of_birth")
	private String date_of_birth;

	@JsonProperty("aadhaar_number")
	private String aadhaar_number;

	@JsonProperty("allotment_startdate")
	private String allotment_startdate;

	@JsonProperty("allotment_enddate")
	private String allotment_enddate;

	@JsonProperty("posession_startdate")
	private String posession_startdate;

	@JsonProperty("posession_enddate")
	private String posession_enddate;

	@JsonProperty("allotmen_number")
	private String allotmen_number;

	@JsonProperty("application_status")
	private String application_status;

	@JsonProperty("active_state")
	private String active_state;

	@JsonProperty("is_primary_owner")
	private String is_primary_owner;

	@JsonProperty("monthly_rent")
	private String monthly_rent;

	@JsonProperty("revision_period")
	private String revision_period;

	@JsonProperty("revision_percentage")
	private String revision_percentage;

	@Valid
	@JsonProperty("payment")
	private List<Payment> payment;

}
