package org.egov.ps.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.ps.model.AuctionBidder;
import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class AuctionRowMapper implements ResultSetExtractor<List<AuctionBidder>> {

	@Override
	public List<AuctionBidder> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<AuctionBidder> bidders = new ArrayList<AuctionBidder>();
		while (rs.next()) {
			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
					.createdTime(rs.getLong("createddate")).lastModifiedBy(rs.getString("lastmodifiedby"))
					.lastModifiedTime(rs.getLong("lastmodifieddate")).build();

			AuctionBidder auction = AuctionBidder.builder().auditDetails(auditdetails)
					.propertyId(rs.getString("propertyid")).description(rs.getString("description"))
					.emdValidityDate(rs.getLong("emdValidityDate")).id(rs.getString("auctionid"))
					.bidderName(rs.getString("bidderName")).refundStatus(rs.getString("refundStatus"))
					.depositDate(rs.getLong("depositDate")).depositedEMDAmount(rs.getBigDecimal("depositedEMDAmount"))
					.build();
			bidders.add(auction);
		}
		return bidders;
	}
}
