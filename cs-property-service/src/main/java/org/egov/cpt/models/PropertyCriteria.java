package org.egov.cpt.models;

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
	
	@Builder.Default
	private String tenantId = "ch.chandigarh";

	private String transitNumber;

	private String colony;

	private String phone;

	private String name;

	private List<String> state;

	private Long offset;

	private Long limit;

	private String propertyId;
	
	private List<String> relations;
	
	private String permanet;
	
	@Builder.Default
	private String createdBy="";
	
	public boolean isEmpty() {
		return (this.transitNumber == null && this.colony == null && this.phone == null
				&& this.name == null && this.state == null && this.offset == null
				&& this.limit == null && this.propertyId == null);
	}
	

}
