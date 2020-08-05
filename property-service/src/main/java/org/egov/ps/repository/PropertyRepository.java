package org.egov.ps.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.producer.Producer;
import org.egov.ps.workflow.WorkflowIntegrator;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PropertyRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private Producer producer;
	
	@Autowired
	private PropertyQueryBuilder queryBuilder;
	
	@Autowired
	private PropertyRowMapper rowMapper;
	
	@Autowired
	private Configuration config;
	
	@Autowired
	WorkflowIntegrator workflowIntegrator;
	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<Object> getProperties(PropertyCriteria criteria) {

		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = queryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, rowMapper);
	}

}
