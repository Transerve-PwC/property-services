package org.egov.ps.repository;

import java.util.Map;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.PropertyCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class PropertyQueryBuilder {

	@Autowired
	private Configuration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pmodified_time desc) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > :start AND offset_ <= :end";

	private static final String SEARCH_QUERY = SELECT + "pt.*,ptdl.*,ownership.*,od.*,doc.*,cc.*,pd.*,"

			+ " pt.id as pid, pt.file_number, pt.tenantid as pttenantid, pt.category, pt.sub_category, pt.site_number, pt.sector_number, pt.state, pt.action,"
			+ " pt.created_by as pcreated_by, pt.created_time as pcreated_time, pt.last_modified_by as pmodified_by, pt.last_modified_time as pmodified_time,"

			+ " ptdl.id as pdid, ptdl.property_id as pdproperty_id, ptdl.property_type as pdproperty_type,"
			+ " ptdl.tenantid as pdtenantid, ptdl.type_of_allocation, ptdl.mode_of_auction, ptdl.scheme_name, ptdl.date_of_auction, ptdl.area_sqft, ptdl.rate_per_sqft, ptdl.last_noc_date, ptdl.service_category,"

			+ " ownership.id as oid, ownership.property_details_id as oproperty_details_id,"
			+ " ownership.tenantid as otenantid, ownership.serial_number as oserial_number,"
			+ " ownership.share as oshare, ownership.cp_number as ocp_number, ownership.state as ostate, ownership.action as oaction,"
			+ " ownership.created_by as ocreated_by, ownership.created_time as ocreated_time, ownership.last_modified_by as omodified_by, ownership.last_modified_time as omodified_time,"

			+ " od.id as odid, od.owner_id as odowner_id,"
			+ " od.owner_name as odowner_name, od.tenantid as odtenantid,"
			+ " od.guardian_name, od.guardian_relation, od.mobile_number,"
			+ " od.allotment_number, od.date_of_allotment, od.possesion_date,"
			+ " od.is_current_owner, od.is_master_entry," + " od.due_amount, od.address,"

			+ " doc.id as docid, doc.owner_details_id as docowner_details_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.file_store_id,"
			+ " doc.created_by as dcreated_by, doc.created_time as dcreated_time, doc.last_modified_by as dmodified_by, doc.last_modified_time as dmodified_time,"

			+ " cc.id as ccid, cc.property_details_id as ccproperty_details_id,"
			+ " cc.tenantid as cctenantid, cc.estate_officer_court as ccestate_officer_court,"
			+ " cc.commissioners_court as cccommissioners_court, cc.chief_administartors_court as ccchief_administartors_court, cc.advisor_to_admin_court as ccadvisor_to_admin_court, cc.honorable_district_court as cchonorable_district_court,"
			+ " cc.honorable_high_court as cchonorable_high_court, cc.honorable_supreme_court as cchonorable_supreme_court,"
			+ " cc.created_by as cccreated_by, cc.created_time as cccreated_time, cc.last_modified_by as ccmodified_by, cc.last_modified_time as ccmodified_time,"

			+ " pd.id as pdid, pd.property_details_id as pdproperty_details_id,"
			+ " pd.tenantid as pdtenantid, pd.new_owner_name as pdnew_owner_name,"
			+ " pd.new_owner_father_name as pdnew_owner_father_name, pd.new_owner_address as pdnew_owner_address, pd.new_owner_mobile_number as pdnew_owner_mobile_number, pd.seller_name as pdseller_name,"
			+ " pd.seller_father_name as pdseller_father_name, pd.percentage_of_share as pdpercentage_of_share, pd.mode_of_transfer as pdmode_of_transfer, pd.registration_number as pdregistration_number, pd.date_of_registration as pddate_of_registration,"
			+ " pd.created_by as pdcreated_by, pd.created_time as pdcreated_time, pd.last_modified_by as pdmodified_by, pd.last_modified_time as pdmodified_time"

			+ " FROM cs_pm_property_v1 pt " + INNER_JOIN 
			+ " cs_pm_property_details_v1 ptdl ON pt.id =ptdl.property_id " + INNER_JOIN 
			+ " cs_pm_owner_v1 ownership ON ptdl.id=ownership.property_details_id " + LEFT_JOIN
			+ " cs_pm_owner_details_v1 od ON ownership.id = od.owner_id " + LEFT_JOIN
			+ " cs_pm_owner_documents_v1 doc ON od.id=doc.owner_details_id " + LEFT_JOIN
			+ " cs_pm_court_case_v1 cc ON ptdl.id=cc.property_details_id " + LEFT_JOIN
			+ " cs_pm_purchase_details_v1 pd ON ptdl.id=pd.property_details_id";

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList, PropertyCriteria criteria) {

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

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getFileNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.file_number=:filNumber");
			preparedStmtList.put("filNumber", criteria.getFileNumber());
		}

		if (null != criteria.getCategory()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.category = :category");
			preparedStmtList.put("category", criteria.getCategory());
		}

		if (null != criteria.getOwnerName()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.owner_name = :name");
			preparedStmtList.put("name", criteria.getOwnerName());
		}

		if (null != criteria.getMobileNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("od.mobile_number = :phone");
			preparedStmtList.put("phone", criteria.getMobileNumber()); //TODO: change phone to name in this line only
		}

		if (null != criteria.getState()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.state = :state");
			preparedStmtList.put("state", criteria.getState());
		}

		if (null != criteria.getPropertyId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.id = :id");
			preparedStmtList.put("id", criteria.getPropertyId());
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

}