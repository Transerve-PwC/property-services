package org.egov.ps.repository;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.ps.model.Auction;
import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class AuctionRowMapper implements ResultSetExtractor<List<Auction>> {

	@Override
	public List<Auction> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Auction> auctionMap = new LinkedHashMap<>();

		while (rs.next()) {
			if (hasColumn(rs, "auctionid")) {
				String auctionId = rs.getString("auctionid");
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
						.createdTime(rs.getLong("createddate")).lastModifiedBy(rs.getString("lastmodifiedby"))
						.lastModifiedTime(rs.getLong("lastmodifieddate")).build();
				
				Auction auction = Auction.builder()
					   .auditDetails(auditdetails)
					   .propertyId(rs.getString("propertyid"))
					   .tenantId(rs.getString("tenantid"))
					   .auctionDescription(rs.getString("auctionDescription"))
					   .fileNumber(rs.getString("filenumber"))
					   .emdValidityDate(rs.getLong("emdValidityDate"))
					   .id(rs.getString("auctionid"))
					   .participatedBidders(rs.getString("participatedBidders"))
					   .refundStatus(rs.getString("refundStatus"))
					   .depositDate(rs.getLong("depositDate"))
					   .depositedEMDAmount(rs.getBigDecimal("depositedEMDAmount"))
					   .build();
				auctionMap.put(auctionId, auction);
			}			
		}
		return new ArrayList<>(auctionMap.values());
	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
