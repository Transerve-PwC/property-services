package org.egov.ps.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.ps.model.Application;
import org.egov.ps.model.ApplicationCriteria;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PropertyRepository {

	@Autowired
	private PropertyQueryBuilder propertyQueryBuilder;

	@Autowired
	private PropertyRowMapper propertyRowMapper;
	
	@Autowired
	private ApplicationQueryBuilder applicationQueryBuilder;

	@Autowired
	private ApplicationRowMapper applicationRowMapper;

	@Autowired
	WorkflowIntegrator workflowIntegrator;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Property> getProperties(PropertyCriteria criteria) {

		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = propertyQueryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, propertyRowMapper);
	}

	public Property findPropertyById(String branchType, String propertyId) {
		PropertyCriteria propertySearchCriteria = PropertyCriteria.builder()
														.propertyId(propertyId)
														.branchType(branchType)
														.build();
		List<Property> properties = this.getProperties(propertySearchCriteria);
		if (properties == null || properties.isEmpty()) {
			return null;
		}
		return properties.get(0);
	}

	public List<Application> getApplications(ApplicationCriteria criteria) {
		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = applicationQueryBuilder.getApplicationSearchQuery(criteria, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, applicationRowMapper);
	}
}
