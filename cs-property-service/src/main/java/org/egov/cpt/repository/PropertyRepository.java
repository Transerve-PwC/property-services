package org.egov.cpt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PropertyRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PropertyQueryBuilder queryBuilder;

	@Autowired
	private PropertyRowMapper rowMapper;
	
	@Autowired
	private DuplicateCopyPropertyRowMapper duplicateCopyPropertyRowMapper;

	public List<Property> getProperties(PropertyCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}
	
	public List<DuplicateCopy> getDuplicateCopyProperties(DuplicateCopySearchCriteria criteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getDuplicateCopyPropertySearchQuery(criteria, preparedStmtList);
		log.info("SearchQuery:"+query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), duplicateCopyPropertyRowMapper);
	}
	
	public List<Mortgage> getMortgageProperties(DuplicateCopySearchCriteria criteria) {
		/*List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getDuplicateCopyPropertySearchQuery(criteria, preparedStmtList);
		log.info("SearchQuery:"+query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), duplicateCopyPropertyRowMapper);*/
		return null;
	}
}
