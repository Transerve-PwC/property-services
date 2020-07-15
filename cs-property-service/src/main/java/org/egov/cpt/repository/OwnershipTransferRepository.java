package org.egov.cpt.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.Owner;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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

	@Autowired
	private Producer producer;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	WorkflowIntegrator workflowIntegrator;

	public List<Owner> searchOwnershipTransfer(DuplicateCopySearchCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getOwnershipTransferSearchQuery(criteria, preparedStmtList);
		log.info("OwnershipTransferSearchQuery: " + query);
		return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	}

	public void update(OwnershipTransferRequest ownershipTransferRequest,
			Map<String, Boolean> idToIsStateUpdatableMap) {
		RequestInfo requestInfo = ownershipTransferRequest.getRequestInfo();
		List<Owner> owners = ownershipTransferRequest.getOwners();

		List<Owner> ownersForStatusUpdate = new LinkedList<>();
		List<Owner> ownersForUpdate = new LinkedList<>();
		List<Owner> ownersForAdhocChargeUpdate = new LinkedList<>();

		for (Owner owner : owners) {
			if (idToIsStateUpdatableMap.get(owner.getId())) {
				ownersForUpdate.add(owner);
			} else if (owner.getApplicationAction().equalsIgnoreCase(PTConstants.ACTION_ADHOC))
				ownersForAdhocChargeUpdate.add(owner);
			else {
				ownersForStatusUpdate.add(owner);
			}
		}

		if (!CollectionUtils.isEmpty(ownersForUpdate))
			producer.push(config.getOwnershipTransferUpdateTopic(),
					new OwnershipTransferRequest(requestInfo, ownersForUpdate));

		if (!CollectionUtils.isEmpty(ownersForStatusUpdate))
			workflowIntegrator.callOwnershipTransferWorkFlow(ownershipTransferRequest);

//		if (!ownersForAdhocChargeUpdate.isEmpty())
//			producer.push(config.getUpdateAdhocTopic(),
//					new OwnershipTransferRequest(requestInfo, ownersForAdhocChargeUpdate));

	}

}
