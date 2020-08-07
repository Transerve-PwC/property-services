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

	private List<String> state;

	private Long offset;

	private Long limit;

	private String propertyId;

	private List<String> relations;

	private String tenantId;

	private String userId;

	public boolean isEmpty() {
		return (this.fileNumber == null && this.category == null && this.mobileNumber == null && this.ownerName == null
				&& this.state == null && this.offset == null && this.limit == null && this.propertyId == null
				&& this.relations == null && this.tenantId == null && this.userId == null);
	}
}
