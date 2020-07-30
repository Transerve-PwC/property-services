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
public class NoticeSearchCriteria {
	
	@Builder.Default
	private String tenantId = "ch.chandigarh";

	private String transitNumber;

	private String colony;

	private String memoNumber;

	private String applicantMobNo;

	private String propertyId;

	private String noticeId;

	private Long offset;

	private Long limit;

	public boolean isEmpty() {
		return (this.transitNumber == null && this.colony == null 
				&& this.memoNumber == null && this.applicantMobNo == null && this.propertyId == null
				&& this.noticeId == null && this.offset == null && this.limit == null);
	}

}
