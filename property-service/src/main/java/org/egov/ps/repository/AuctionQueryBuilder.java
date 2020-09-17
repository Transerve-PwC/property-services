package org.egov.ps.repository;

import org.springframework.stereotype.Component;

@Component
public class AuctionQueryBuilder {

	public String getInsertQuery() {
		return "INSERT INTO cs_ep_auction (id, auctionDescription, participatedBidders, depositedEMDAmount, depositDate, "
				+ "emdValidityDate, refundStatus,createdby, lastmodifiedby, createddate, lastmodifieddate) values"
				+ "(nextval('seq_cs_ep_auction'), :auctionDescription, :participatedBidders, :depositedEMDAmount, :depositDate,"
				+ " :emdValidityDate, :refundStatus,:createdby, :lastmodifiedby, :createddate, :lastmodifieddate)";
	}

}
