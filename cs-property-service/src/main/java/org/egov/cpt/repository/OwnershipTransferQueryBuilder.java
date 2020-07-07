package org.egov.cpt.repository;

import java.util.List;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.OwnershipTransferSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class OwnershipTransferQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY oid) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

//  reference from pt-services-v2 package:package org.egov.pt.repository.builder;
	private static final String SEARCH_QUERY = SELECT + "pt.*,ownership.*,od.*,"

			+ " pt.id as pid, pt.transit_number,"

			+ " ownership.id as oid, ownership.property_id as oproperty_id,"
			+ " ownership.tenantid as otenantid, ownership.allotmen_number as oallotmen_number,"
			+ " ownership.active_state as oactive_state, ownership.is_primary_owner as ois_primary_owner,"
			+ " ownership.application_state, ownership.application_action,"
			+ " ownership.created_by as ocreated_by, ownership.created_date as ocreated_date, ownership.modified_by as omodified_by, ownership.modified_date as omodified_date,"

			+ " od.id as odid, od.property_id as odproperty_id," + " od.owner_id odowner_id, od.tenantid as odtenantid,"
			+ " od.name, od.email, od.phone," + " od.gender, od.date_of_birth, od.aadhaar_number,"
			+ " od.allotment_startdate, od.allotment_enddate," + " od.posession_startdate, od.posession_enddate,"
			+ " od.monthly_rent, od.revision_period, od.revision_percentage, od.father_or_husband, od.relation,"
			+ " od.relation_with_deceased_allottee, od.date_of_death_allottee, od.application_number, od.application_type, od.permanent"

//			+ " FROM cs_pt_ownership_v1 ownership " + INNER_JOIN
//			+ " cs_pt_ownershipdetails_v1 od ON ownership.id=od.owner_id ";

			+ " FROM cs_pt_property_v1 pt " + INNER_JOIN
			+ " cs_pt_ownership_v1 ownership ON pt.id=ownership.property_id " + LEFT_JOIN
			+ " cs_pt_ownershipdetails_v1 od ON ownership.id = od.owner_id ";

	private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			OwnershipTransferSearchCriteria criteria) {

		if (criteria.getLimit() == null && criteria.getOffset() == null)
			return query;

		Long limit = config.getDefaultLimit();
		Long offset = config.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			limit = config.getMaxSearchLimit();

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		preparedStmtList.add(offset);
		preparedStmtList.add(limit + offset);

		return finalQuery;
	}

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getOwnershipTransferSearchQuery(OwnershipTransferSearchCriteria criteria,
			List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.application_number = ?");
			preparedStmtList.add(criteria.getApplicationNumber());
		}

		if (null != criteria.getApplicantMobNo()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.phone = ?");
			preparedStmtList.add(criteria.getApplicantMobNo());
		}

		if (null != criteria.getPropertyId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.id = ?");
			preparedStmtList.add(criteria.getPropertyId());
		}

		if (!ObjectUtils.isEmpty(criteria.getTransitNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.transit_number = ?");
			preparedStmtList.add(criteria.getTransitNumber());
		}

		if (!ObjectUtils.isEmpty(criteria.getStatus())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("ownership.application_state = ?");
			preparedStmtList.add(criteria.getStatus());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}

	private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}
}
