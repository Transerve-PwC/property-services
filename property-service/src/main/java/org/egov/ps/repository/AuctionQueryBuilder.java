package org.egov.ps.repository;

import java.util.Map;

import org.egov.ps.model.AuctionSearchCritirea;
import org.springframework.stereotype.Component;

@Component
public class AuctionQueryBuilder {

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = " INNER JOIN ";
	private static final String WHERE = " WHERE ";

	private static final String AUT_COLUMNS = "aut.id as auctionid,aut.property_id as propertyid,aut.file_number as filenumber,"
			+ "aut.tenant_id as tenantid,aut.description as description,"
			+ "aut.bidder_name as bidderName,aut.deposited_emd_amount as depositedEMDAmount,"
			+ "aut.deposit_date as depositDate,aut.emdValidity_date as emdValidityDate,aut.refund_status as refundStatus,"
			+ "aut.created_by as createdby,aut.last_modified_by as lastmodifiedby,aut.created_date as createddate,"
			+ "aut.last_modified_date as lastmodifieddate";

	private static final String AUT_PT_JOIN = " FROM cs_ep_auction aut " + INNER_JOIN
			+ " cs_ep_property_v1 pt ON pt.id=aut.property_id";

	public String getAuctionSearchQuery(AuctionSearchCritirea auctionSearchCritirea,
			Map<String, Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(SELECT);
		builder.append(AUT_COLUMNS);
		builder.append(AUT_PT_JOIN);
		if (null != auctionSearchCritirea.getFileNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" pt.file_number=:filenumber ");
			preparedStmtList.put("filenumber", auctionSearchCritirea.getFileNumber());
		}
		if (null != auctionSearchCritirea.getAuctionId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" aut.id=:auctionid ");
			preparedStmtList.put("auctionid", auctionSearchCritirea.getAuctionId());
		}

		return builder.toString();
	}

	private static void addClauseIfRequired(Map<String, Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(WHERE);
		else {
			queryString.append(" AND ");
		}
	}

}
