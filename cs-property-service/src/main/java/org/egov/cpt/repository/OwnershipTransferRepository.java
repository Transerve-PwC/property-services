package org.egov.cpt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnershipTransferSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnershipTransferRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OwnershipTransferQueryBuilder queryBuilder;

	@Autowired
	private OwnershipTransferRowMapper rowMapper;

	public List<Owner> searchOwnershipTransfer(OwnershipTransferSearchCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getOwnershipTransferSearchQuery(criteria, preparedStmtList);
		log.info("OwnershipTransferSearchQuery: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}

}
