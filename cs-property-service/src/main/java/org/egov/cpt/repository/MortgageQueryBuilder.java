package org.egov.cpt.repository;

import java.util.List;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.PropertyCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class MortgageQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pid) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

	private static final String MORTGAGE_SEARCH_QUERY = SELECT + "mg.*,ap.*,doc.*,pt.*,"
			+ " mg.id as mgid, mg.propertyid, mg.tenantid as mgtenantid, mg.state, mg.action,mg.application_number as app_number,"
			
			+ " pt.id as pid, pt.transit_number,"

			+ " ap.id as aid, ap.mortgage_id as mg_id,ap.tenantid as aptenantid,"
			+ " ap.name,ap.email,ap.mobileno,ap.guardian,ap.relationship,ap.aadhaar_number as adhaarnumber,"

			+ " doc.id as docId, doc.tenantId as doctenantid,doc.documenttype as doctype , doc.filestoreid as doc_filestoreid,"
			+ " doc.mortgage_id as doc_mgid , doc.active as doc_active"

			+ " FROM cs_pt_mortgage_application mg " + INNER_JOIN
			+ " cs_pt_property_v1 pt on mg.propertyid=pt.id "+ INNER_JOIN
			+ " cs_pt_mortgage_applicant ap ON mg.id =ap.mortgage_id " + LEFT_JOIN
			+ " cs_pt_mortgage_douments doc ON doc.mortgage_id =  mg.id";


	private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			DuplicateCopySearchCriteria criteria) {

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


	private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}

	public String getMortgageSearchQuery(DuplicateCopySearchCriteria criteria,
			List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(MORTGAGE_SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getPropertyId())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("mg.propertyid=?");
			preparedStmtList.add(criteria.getPropertyId());
		}
		if (null != criteria.getAppId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("mg.id=?");
			preparedStmtList.add(criteria.getAppId());
		}
		if (null != criteria.getTransitNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.transit_number=?");
			preparedStmtList.add(criteria.getTransitNumber());
		}
		if (null != criteria.getApplicationNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("mg.application_number=?");
			preparedStmtList.add(criteria.getApplicationNumber());
		}
		if (null != criteria.getColony()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.colony=?");
			preparedStmtList.add(criteria.getColony());
		}
		if (null != criteria.getApplicantMobNo()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("ap.mobileno=?");
			preparedStmtList.add(criteria.getApplicantMobNo());
		}
		if (null != criteria.getStatus()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("mg.state=?");
			preparedStmtList.add(criteria.getStatus());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}
}
