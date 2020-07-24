package org.egov.cpt.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
	
	@Autowired
	private DuplicateCopyQueryBuilder duplicatecopyQueryBuilder;
	
	@Autowired
	private MortgageRowMapper mortgageRowMapper;
	
	@Autowired
	private MortgageQueryBuilder mortgageQueryBuilder;

	@Autowired
	private Producer producer;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	WorkflowIntegrator workflowIntegrator;
	
	@Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Property> getProperties(PropertyCriteria criteria) {

		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = queryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, rowMapper);
	}

	public List<DuplicateCopy> getDuplicateCopyProperties(DuplicateCopySearchCriteria criteria) {
		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = duplicatecopyQueryBuilder.getDuplicateCopyPropertySearchQuery(criteria, preparedStmtList);
//		log.info("SearchQuery:"+query);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, duplicateCopyPropertyRowMapper);
	}

	public List<Mortgage> getMortgageProperties(DuplicateCopySearchCriteria criteria) {
		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = mortgageQueryBuilder.getMortgageSearchQuery(criteria, preparedStmtList);
		log.info("MortgageSearchQuery:"+query);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, mortgageRowMapper);
	}

	public void updateDcPayment(DuplicateCopyRequest duplicateCopyRequest,
			Map<String, Boolean> idToIsStateUpdatableMapDc) {
		RequestInfo requestInfo = duplicateCopyRequest.getRequestInfo();
		List<DuplicateCopy> dcApplications = duplicateCopyRequest.getDuplicateCopyApplications();

		List<DuplicateCopy> dcApplicationsForUpdate = new LinkedList<>();

		for (DuplicateCopy dcApplication : dcApplications) {
			if (idToIsStateUpdatableMapDc.get(dcApplication.getId())) {
				dcApplicationsForUpdate.add(dcApplication);
			}
		}

		if (!CollectionUtils.isEmpty(dcApplicationsForUpdate)) {
			workflowIntegrator
					.callDuplicateCopyWorkFlow(new DuplicateCopyRequest(requestInfo, dcApplicationsForUpdate));
			producer.push(config.getUpdateDuplicateCopyTopic(),
					new DuplicateCopyRequest(requestInfo, dcApplicationsForUpdate));
		}
	}
}
