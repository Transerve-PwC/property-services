package org.egov.ps.repository;

import java.util.Map;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.ApplicationCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ApplicationQueryBuilder {

	@Autowired
	private Configuration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY applast_modified_time desc) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > :start AND offset_ <= :end";

	private static final String APPLICATION_SEARCH_QUERY = SELECT + "app.*, pt.*, doc.*,"

			+ " app.id as appid, app.tenantid as apptenantid, app.property_id as appproperty_id,"
			+ " app.application_number as appapplication_number,"
			+ " app.branch_type as appbranch_type, app.module_type as appmodule_type, app.application_type as appapplication_type,"
			+ " app.comments as appcomments, app.hardcopy_received_date as apphardcopy_received_date,"
			+ " app.state as appstate, app.action as appaction,"

			+ " app.application_details as appapplication_details,"

			+ " app.created_by as appcreated_by, app.last_modified_by as applast_modified_by,"
			+ " app.created_time as appcreated_time, app.last_modified_time as applast_modified_time,"

			+ " pt.id as ptid, pt.file_number as ptfile_number,"

			+ " doc.id as docid, doc.reference_id as docapplication_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.file_store_id, doc.property_id as docproperty_id,"
			+ " doc.created_by as dcreated_by, doc.created_time as dcreated_time, doc.last_modified_by as dmodified_by, doc.last_modified_time as dmodified_time"

			+ " FROM cs_pm_application_v1 app " + INNER_JOIN + " cs_pm_property_v1 pt on app.property_id = pt.id "
			+ LEFT_JOIN + " cs_pm_documents_v1 doc ON app.id=doc.reference_id ";

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList,
			ApplicationCriteria criteria) {

		/*
		 * if (criteria.getLimit() == null && criteria.getOffset() == null) return
		 * query;
		 */

		Long limit = config.getDefaultLimit();
		Long offset = config.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			limit = config.getMaxSearchLimit();

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		preparedStmtList.put("start", offset);
		preparedStmtList.put("end", limit + offset);

		return finalQuery;
	}

	private static void addClauseIfRequired(Map<String, Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}

	public String getApplicationSearchQuery(ApplicationCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(APPLICATION_SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getPropertyId())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("app.property_id=:prId");
			preparedStmtList.put("prId", criteria.getPropertyId());
		}
		if (!ObjectUtils.isEmpty(criteria.getApplicationId())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("app.id=:appId");
			preparedStmtList.put("appId", criteria.getApplicationId());
		}
		if (null != criteria.getTenantId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("app.tenantid=:tenantId");
			preparedStmtList.put("tenantId", criteria.getTenantId());
		}
		if (null != criteria.getFileNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.file_number=:fileNumber");
			preparedStmtList.put("fileNumber", criteria.getFileNumber());
		}
		if (null != criteria.getApplicationNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("app.application_number=:appNumber");
			preparedStmtList.put("appNumber", criteria.getApplicationNumber());
		}
		if (null != criteria.getState()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("app.state IN (:state)");
			preparedStmtList.put("state", criteria.getState());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}
}
