package org.egov.ps.model;


import java.util.List;

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

	private String fileNumber;

	private String category;

	private String mobileNumber;

	private String ownerName;

	private String state;

	private Long offset;

	private Long limit;

	private String propertyId;
	
	private List<String> relations;
	
}
