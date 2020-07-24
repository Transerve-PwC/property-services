package org.egov.cpt.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopySearchCriteria;
import org.egov.cpt.models.PropertyCriteria;
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
			+ "WHERE offset_ > :offset1 AND offset_ <= :offset2";

//  reference from pt-services-v2 package:package org.egov.pt.repository.builder;
	private static final String SEARCH_QUERY = SELECT + "pt.*,ptdl.*,ownership.*,od.*,address.*,doc.*,"

			+ " pt.id as pid, pt.transit_number, pt.tenantid as pttenantid, pt.colony, pt.master_data_state, pt.master_data_action,"
			+ " pt.created_by as pcreated_by, pt.created_date as pcreated_date, pt.modified_by as pmodified_by, pt.modified_date as pmodified_date,"

			+ " ptdl.id as pdid, ptdl.property_id as pdproperty_id, ptdl.transit_number as pdtransit_number,"
			+ " ptdl.tenantid as pdtenantid, ptdl.area, ptdl.rent_per_sqyd, ptdl.current_owner, ptdl.floors, ptdl.additional_details,"

			+ " ownership.id as oid, ownership.property_id as oproperty_id,"
			+ " ownership.tenantid as otenantid, ownership.allotmen_number as oallotmen_number,"
			+ " ownership.active_state as oactive_state, ownership.is_primary_owner as ois_primary_owner,"
			+ " ownership.created_by as ocreated_by, ownership.created_date as ocreated_date, ownership.modified_by as omodified_by, ownership.modified_date as omodified_date,"

			+ " od.id as odid, od.property_id as odproperty_id," + " od.owner_id odowner_id, od.tenantid as odtenantid,"
			+ " od.name, od.email, od.phone," + " od.gender, od.date_of_birth, od.aadhaar_number,"
			+ " od.allotment_startdate, od.allotment_enddate," + " od.posession_startdate, od.posession_enddate,"
			+ " od.monthly_rent, od.revision_period, od.revision_percentage, od.father_or_husband, od.relation,"

			+ " address.id as aid, address.property_id as aproperty_id, address.transit_number as atransit_number,"
			+ " address.tenantid as atenantid, address.colony, address.area as addressArea, address.district,"
			+ " address.state, address.country, address.pincode, address.landmark,"

			+ " doc.id as docid, doc.property_id as docproperty_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.fileStore_id, doc.document_uid"

			+ " FROM cs_pt_property_v1 pt " + INNER_JOIN + " cs_pt_propertydetails_v1 ptdl ON pt.id =ptdl.property_id "
			+ INNER_JOIN + " cs_pt_ownership_v1 ownership ON pt.id=ownership.property_id " + LEFT_JOIN
			+ " cs_pt_ownershipdetails_v1 od ON ownership.id = od.owner_id " + LEFT_JOIN
			+ " cs_pt_address_v1 address ON pt.id=address.property_id " + LEFT_JOIN
			+ " cs_pt_application_documents_v1 doc ON pt.id=doc.property_id "
//			+ " WHERE "
	;

	private static final String DUPLICATE_COPY_SEARCH_QUERY = SELECT + "dca.*,ap.*,doc.*,pt.*,address.*,"
			+ " dca.id as appid, dca.property_id, dca.tenantid as pttenantid, dca.state, dca.action,dca.application_number as app_number,"

			+ " pt.id as pid, pt.transit_number,pt.colony,"

			+ " address.pincode,"

			+ " ap.id as aid, ap.application_id as app_id,ap.tenantid as aptenantid,"
			+ " ap.name,ap.email,ap.mobileno,ap.guardian,ap.relationship,ap.aadhaar_number as adhaarnumber,"

			+ " doc.id as docId, doc.tenantId as doctenantid,doc.documenttype as doctype , doc.filestoreid as doc_filestoreid,"
			+ " doc.application_id as doc_applid , doc.active as doc_active"

			+ " FROM cs_pt_duplicate_ownership_application dca " + INNER_JOIN
			+ " cs_pt_property_v1 pt on dca.property_id=pt.id " + INNER_JOIN
			+ " cs_pt_address_v1 address ON pt.id=address.property_id " + LEFT_JOIN
			+ " cs_pt_duplicatecopy_applicant ap ON dca.id =ap.application_id " + LEFT_JOIN
			+ " cs_pt_duplicatecopy_document doc ON doc.application_id =  dca.id";

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList, PropertyCriteria criteria) {

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

		preparedStmtList.put(":offset1",offset);
		preparedStmtList.put("offset2",limit + offset);

		return finalQuery;
	}

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList,
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

		preparedStmtList.put("offset1",offset);
		preparedStmtList.put("offset2",limit + offset);

		return finalQuery;
	}

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getTransitNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.transit_number=:trnNumber");
			preparedStmtList.put("trnNumber",criteria.getTransitNumber());
		}

		if (null != criteria.getColony()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.colony = :colony");
			preparedStmtList.put("colony",criteria.getColony());
		}

		if (null != criteria.getName()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.name = :name");
			preparedStmtList.put("name",criteria.getName());
		}

		if (null != criteria.getPhone()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.phone = :phone");
			preparedStmtList.put("name",criteria.getPhone());
		}

		if (null != criteria.getState()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.master_data_state = :state");
			preparedStmtList.put("state",criteria.getState());
		}

		if (null != criteria.getPropertyId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.id = :id");
			preparedStmtList.put("id",criteria.getPropertyId());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
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
