package org.egov.cpt.models;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Property {

	@JsonProperty("property_id")
	private String property_id;

	@JsonProperty("transit_number")
	private String transit_number;

	@JsonProperty("colony")
	private String colony;

	@JsonProperty("area")
	private String area;

	@JsonProperty("pin_code")
	private String pin_code;

	@JsonProperty("current_owner")
	private String current_owner;

	@JsonProperty("state")
	private String state;

	@JsonProperty("action")
	private String action;

	@Valid
	@JsonProperty("owner")
	private List<Owner> owner;

}
