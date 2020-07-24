package org.egov.cpt.repository;

import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class DuplicateCopyQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY dcModifiedTime desc) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > :offset1 AND offset_ <= :offset2";

	private static final String DUPLICATE_COPY_SEARCH_QUERY = SELECT + "dca.*,ap.*,doc.*,pt.*,address.*,"
			+ " dca.id as appid, dca.property_id, dca.tenantid as pttenantid, dca.state, dca.action,"
			+ " dca.application_number as app_number,dca.modified_time as dcModifiedTime,"

			+ " pt.id as pid, pt.transit_number,pt.colony,pt.modified_date as pmodified_date,"

			+ " address.pincode, address.area,"

			+ " ap.id as aid, ap.application_id as app_id,ap.tenantid as aptenantid,"
			+ " ap.name,ap.email,ap.mobileno,ap.guardian,ap.relationship,ap.aadhaar_number as adhaarnumber,"

			+ " doc.id as docId, doc.tenantId as doctenantid,doc.documenttype as doctype , doc.filestoreid as doc_filestoreid,"
			+ " doc.application_id as doc_applid , doc.active as doc_active"

			+ " FROM cs_pt_duplicate_ownership_application dca " + INNER_JOIN
			+ " cs_pt_property_v1 pt on dca.property_id=pt.id " + INNER_JOIN
			+ " cs_pt_address_v1 address ON pt.id=address.property_id " + LEFT_JOIN
			+ " cs_pt_duplicatecopy_applicant ap ON dca.id =ap.application_id " + LEFT_JOIN
			+ " cs_pt_duplicatecopy_document doc ON doc.application_id =  dca.id";

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList,
			DuplicateCopySearchCriteria criteria) {

		/*if (criteria.getLimit() == null && criteria.getOffset() == null)
			return query;*/

		Long limit = config.getDefaultLimit();
		Long offset = config.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			limit = config.getMaxSearchLimit();

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		preparedStmtList.put("offset1",offset);
		preparedStmtList.put("offset2",limit + offset);

		return finalQuery;
	}
	
	private static void addClauseIfRequired(Map<String, Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}

	public String getDuplicateCopyPropertySearchQuery(DuplicateCopySearchCriteria criteria,
			Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(DUPLICATE_COPY_SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getPropertyId())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("dca.property_id=:prId");
			preparedStmtList.put("prId",criteria.getPropertyId());
		}
		if (null != criteria.getAppId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("dca.id=:id");
			preparedStmtList.put("id",criteria.getAppId());
		}
		if (null != criteria.getTransitNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.transit_number=:trnNumber");
			preparedStmtList.put("trnNumber",criteria.getTransitNumber());
		}
		if (null != criteria.getApplicationNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("dca.application_number=:appNumber");
			preparedStmtList.put("appNumber",criteria.getApplicationNumber());
		}
		if (null != criteria.getColony()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.colony=:colony");
			preparedStmtList.put("colony",criteria.getColony());
		}
		if (null != criteria.getApplicantMobNo()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("ap.mobileno=:mobNo");
			preparedStmtList.put("mobNo",criteria.getApplicantMobNo());
		}
		if (null != criteria.getStatus()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("dca.state IN (:state)");
			preparedStmtList.put("state",criteria.getStatus());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}
}
