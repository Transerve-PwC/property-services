package org.egov.ps.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.model.AuctionBidder;
import org.egov.ps.model.AuctionSearchCritirea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuctionRepository {

	@Autowired
	private AuctionQueryBuilder auctionQueryBuilder;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private AuctionRowMapper auctionRowMapper;

	public List<AuctionBidder> search(AuctionSearchCritirea auctionSearchCritirea) {
		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = auctionQueryBuilder.getAuctionSearchQuery(auctionSearchCritirea, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, auctionRowMapper);
	}

}
