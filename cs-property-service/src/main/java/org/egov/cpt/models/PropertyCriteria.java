package org.egov.cpt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyCriteria {

	private String transitNumber;

	private String colony;

	private String phone;

	private String name;

	private String state;

	private Long offset;

	private Long limit;
	
	private String id;
	
	private String propertyId;

}
