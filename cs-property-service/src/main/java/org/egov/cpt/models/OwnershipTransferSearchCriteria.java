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
public class OwnershipTransferSearchCriteria {

	private String transitNumber;

	private String colony;

	private String status;

	private String applicationNumber;

	private String applicantMobNo;

	private String propertyId;

	private String appId;

	private Long offset;

	private Long limit;

}
