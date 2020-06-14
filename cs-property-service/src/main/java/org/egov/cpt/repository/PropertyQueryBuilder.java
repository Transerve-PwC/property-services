package org.egov.cpt.repository;

import java.util.List;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class PropertyQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pid) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

//  reference from pt-services-v2 package:package org.egov.pt.repository.builder;
	private static final String SEARCH_QUERY = SELECT + "pt.*,ptdl.*,ownership.*,"

			+ " pt.id as pid, pt.transit_number, pt.colony, pt.master_data_state, pt.master_data_action,"

			+ " ptdl.id as pdid, ptdl.property_id as pdproperty_id, ptdl.transit_number as pdtransit_number,"
			+ " ptdl.area, ptdl.rent_per_sqyd, ptdl.current_owner, ptdl.floors, ptdl.additional_details,"

			+ " ownership.id as oid, ownership.property_id as oproperty_id, ownership.owner_id,"
			+ " ownership.name, ownership.email, ownership.phone,"
			+ " ownership.gender, ownership.date_of_birth, ownership.aadhaar_number,"
			+ " ownership.allotment_startdate, ownership.allotment_enddate,"
			+ " ownership.posession_startdate, ownership.posession_enddate, ownership.allotmen_number,"
			+ " ownership.application_status, ownership.active_state, ownership.is_primary_owner,"
			+ " ownership.monthly_rent, ownership.revision_period, ownership.revision_percentage"

			+ " FROM cs_pt_property_v1 pt " + INNER_JOIN + " cs_pt_propertydetails_v1 ptdl ON pt.id =ptdl.property_id "
			+ INNER_JOIN + " cs_pt_ownership_v1 ownership ON ptdl.property_id=ownership.property_id " 
//			+ " WHERE "
			;

	private String addPaginationWrapper(String query, List<Object> preparedStmtList, PropertyCriteria criteria) {

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
	public String getPropertySearchQuery(PropertyCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getTransitNumber())) {
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("pt.transit_number=?");
			preparedStmtList.add(criteria.getTransitNumber());
		}

		if (null != criteria.getColony()) {
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("pt.colony = ?");
			preparedStmtList.add(criteria.getColony());
		}

		if (null != criteria.getName()) {
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("ownership.name = ?");
			preparedStmtList.add(criteria.getName());
		}

		if (null != criteria.getPhone()) {
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("ownership.phone = ?");
			preparedStmtList.add(criteria.getPhone());
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
