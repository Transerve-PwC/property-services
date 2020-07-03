package org.egov.cpt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OwnershipTransferRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OwnershipTransferQueryBuilder queryBuilder;

	@Autowired
	private OwnershipTransferRowMapper rowMapper;

	public List<Property> getProperties(PropertyCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
}
