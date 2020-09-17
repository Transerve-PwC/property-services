package org.egov.ps.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Auction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuctionRepository {

	@Autowired
	private AuctionQueryBuilder auctionQueryBuilder;

	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Auction> create(List<Auction> auctions,RequestInfo requestInfo) {
		if (!auctions.isEmpty()) {
			final String query = auctionQueryBuilder.getInsertQuery();
			
			List<Map<String, Object>> auctionsDbnsertMap = new ArrayList<>(auctions.size());
			for (Auction auction : auctions) {				
				auctionsDbnsertMap.add(
						new MapSqlParameterSource("auctionDescription", auction.getAuctionDescription())
                            .addValue("participatedBidders", auction.getParticipatedBidders())
                            .addValue("depositedEMDAmount", auction.getDepositedEMDAmount())
                            .addValue("depositDate", auction.getDepositDate())
                            .addValue("emdValidityDate", auction.getEmdValidityDate())
                            .addValue("refundStatus", auction.getRefundStatus())                            
                            .addValue("createdby", requestInfo.getUserInfo().getId())
                            .addValue("lastmodifiedby", requestInfo.getUserInfo().getId())
                            .addValue("createddate", new Date().getTime())
                            .addValue("lastmodifieddate", new Date().getTime()).getValues());				
			}
			try {
				namedParameterJdbcTemplate.batchUpdate(query, auctionsDbnsertMap.toArray(new Map[auctions.size()]));
			} catch (DataAccessException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex.getMessage());
			}
		}
		return auctions;
	}
}
