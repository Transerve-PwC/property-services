package org.egov.ps.repository;

import java.util.Map;

import org.egov.ps.model.AuctionSearchCritirea;
import org.egov.ps.web.contracts.AutionSearchRequest;
import org.springframework.stereotype.Component;

@Component
public class AuctionQueryBuilder {

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = " INNER JOIN ";
	private static final String WHERE = " WHERE ";
	

	private static final String AUT_COLUMNS = "aut.id as auctionid,aut.property_id as propertyid,aut.file_number as filenumber,"
			+ "aut.tenant_id as tenantid,aut.auction_description as auctionDescription,\r\n"
			+ "aut.participated_bidders as participatedBidders,aut.deposited_emd_amount as depositedEMDAmount,\r\n"
			+ "aut.deposit_date as depositDate,aut.emdValidity_date as emdValidityDate,aut.refund_status as refundStatus,\r\n"
			+ "aut.created_by as createdby,aut.last_modified_by as lastmodifiedby,aut.created_date as createddate,\r\n"
			+ "aut.last_modified_date as lastmodifieddate";

	private static final String AUT_PT_JOIN = " FROM cs_ep_auction aut " + INNER_JOIN
			+ " cs_ep_property_v1 pt ON pt.id=aut.property_id";

	public String getInsertQuery() {
		return "INSERT INTO cs_ep_auction (id, auction_master_id,auctionDescription, participatedBidders, depositedEMDAmount, depositDate, "
				+ "emdValidityDate, refundStatus,createdby, lastmodifiedby, createddate, lastmodifieddate) values"
				+ "(nextval('seq_cs_ep_auction'), :auctionMasterId,:auctionDescription, :participatedBidders, :depositedEMDAmount, :depositDate,"
				+ " :emdValidityDate, :refundStatus,:createdby, :lastmodifiedby, :createddate, :lastmodifieddate)";
	}

	public String getAuctionSearchQuery(AuctionSearchCritirea auctionSearchCritirea,Map<String, Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(SELECT);
		builder.append(AUT_COLUMNS);
		builder.append(AUT_PT_JOIN);
		builder.append(WHERE);
		builder.append("pt.file_number=:filenumber");
		preparedStmtList.put("filenumber", auctionSearchCritirea.getFileNumber());
		return builder.toString();
	}

	public String getPositionSeqQuery() {
		return "SELECT CURRVAL('seq_cs_ep_auction_master')";
	}

}
