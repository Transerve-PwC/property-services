package org.egov.ps.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.egov.ps.config.Configuration;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.util.PSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyQueryBuilder {

	@Autowired
	private Configuration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = " INNER JOIN ";
	private static final String LEFT_JOIN = " LEFT OUTER JOIN ";

	private static final String PT_ALL = " pt.*, ptdl.*, ";
	private static final String OWNER_ALL = " ownership.*, od.*, doc.*, payment.*,";
	private static final String CC_ALL = " cc.*, ";
	private static final String PD_ALL = "  pd.*, ";

	private static final String PT_COLUMNS = " pt.id as pid, pm_app.branch_type as branch_type, pt.file_number, pt.tenantid as pttenantid, pt.category, pt.sub_category, "
			+ " pt.site_number, pt.sector_number, pt.state as pstate, pt.action as paction, pt.created_by as pcreated_by, pt.created_time as pcreated_time, "
			+ " pt.last_modified_by as pmodified_by, pt.last_modified_time as pmodified_time,"

			+ " ptdl.id as ptdlid, ptdl.property_id as pdproperty_id, ptdl.property_type as pdproperty_type, "
			+ " ptdl.tenantid as pdtenantid, ptdl.type_of_allocation, ptdl.mode_of_auction, ptdl.scheme_name,ptdl.date_of_auction, "
			+ " ptdl.area_sqft, ptdl.rate_per_sqft, ptdl.last_noc_date, ptdl.service_category";

	private static final String OWNER_COLUMNS = " ownership.id as oid, ownership.property_details_id as oproperty_details_id, "
			+ " ownership.tenantid as otenantid, ownership.serial_number as oserial_number, "
			+ " ownership.share as oshare, ownership.cp_number as ocp_number, ownership.state as ostate, ownership.action as oaction, "
			+ " ownership.created_by as ocreated_by, ownership.created_time as ocreated_time, "
			+ " ownership.last_modified_by as omodified_by, ownership.last_modified_time as omodified_time, "

			+ " od.id as odid, od.owner_id as odowner_id,"
			+ " od.owner_name as odowner_name, od.tenantid as odtenantid,"
			+ " od.guardian_name, od.guardian_relation, od.mobile_number,"
			+ " od.allotment_number, od.date_of_allotment, od.possesion_date,"
			+ " od.is_current_owner, od.is_master_entry," + " od.due_amount, od.address, "

			+ " doc.id as docid, doc.reference_id as docowner_details_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.file_store_id, doc.property_id as docproperty_id,"
			+ " doc.created_by as dcreated_by, doc.created_time as dcreated_time, doc.last_modified_by as dmodified_by, doc.last_modified_time as dmodified_time, "

			+ " payment.id as payid, payment.tenantid as paytenantid, payment.owner_details_id as payowner_details_id, "
			+ " payment.gr_due_date_of_payment, payment.gr_payable, payment.gr_amount_of_gr, "
			+ " payment.gr_total_gr, payment.gr_date_of_deposit, payment.gr_delay_in_payment, "
			+ " payment.gr_interest_for_delay, payment.gr_total_amount_due_with_interest, payment.gr_amount_deposited_gr, "
			+ " payment.gr_amount_deposited_intt, payment.gr_balance_gr, payment.gr_balance_intt, "
			+ " payment.gr_total_due, payment.gr_receipt_number, payment.gr_receipt_date, "
			+ " payment.st_rate_of_st_gst, payment.st_amount_of_gst, payment.st_amount_due, "
			+ " payment.st_date_of_deposit, payment.st_delay_in_payment, payment.st_interest_for_delay, "
			+ " payment.st_total_amount_due_with_interest, payment.st_amount_deposited_st_gst, payment.st_amount_deposited_intt, "
			+ " payment.st_balance_st_gst, payment.st_balance_intt, payment.st_total_due, "
			+ " payment.st_receipt_number, payment.st_receipt_date, payment.st_payment_made_by, "
			+ " payment.created_by as paycreated_by, payment.created_time as paycreated_time, payment.last_modified_by as paymodified_by, payment.last_modified_time as paymodified_time ";

	private static final String CC_COLUMNS = " cc.id as ccid, cc.property_details_id as ccproperty_details_id,"
			+ " cc.tenantid as cctenantid, cc.estate_officer_court as ccestate_officer_court,"
			+ " cc.commissioners_court as cccommissioners_court, cc.chief_administartors_court as ccchief_administartors_court, cc.advisor_to_admin_court as ccadvisor_to_admin_court, cc.honorable_district_court as cchonorable_district_court,"
			+ " cc.honorable_high_court as cchonorable_high_court, cc.honorable_supreme_court as cchonorable_supreme_court,"
			+ " cc.created_by as cccreated_by, cc.created_time as cccreated_time, cc.last_modified_by as ccmodified_by, cc.last_modified_time as ccmodified_time ";

	private static final String PD_COLUMNS = " pd.id as pdid, pd.property_details_id as pdproperty_details_id,"
			+ " pd.tenantid as pdtenantid, pd.new_owner_name as pdnew_owner_name,"
			+ " pd.new_owner_father_name as pdnew_owner_father_name, pd.new_owner_address as pdnew_owner_address, pd.new_owner_mobile_number as pdnew_owner_mobile_number, pd.seller_name as pdseller_name,"
			+ " pd.seller_father_name as pdseller_father_name, pd.percentage_of_share as pdpercentage_of_share, pd.mode_of_transfer as pdmode_of_transfer, pd.registration_number as pdregistration_number, pd.date_of_registration as pddate_of_registration,"
			+ " pd.created_by as pdcreated_by, pd.created_time as pdcreated_time, pd.last_modified_by as pdmodified_by, pd.last_modified_time as pdmodified_time ";

	private static final String PT_TABLE = " FROM cs_pm_property_v1 pt " + INNER_JOIN
			+ " cs_pm_property_details_v1 ptdl  ON pt.id =ptdl.property_id " + INNER_JOIN
			+ " cs_pm_application_v1 pm_app ON pt.id= ptdl.property_id ";
	
	private static final String OWNER_TABLE = " cs_pm_owner_v1 ownership  ON ptdl.id=ownership.property_details_id "
			+ LEFT_JOIN + " cs_pm_owner_details_v1 od ON ownership.id = od.owner_id " + LEFT_JOIN
			+ " cs_pm_documents_v1 doc ON od.id=doc.reference_id " + LEFT_JOIN
			+ " cs_pm_payment_v1 payment ON od.id=payment.owner_details_id ";

	private static final String CC_TABLE = " cs_pm_court_case_v1 cc ON ptdl.id=cc.property_details_id ";

	private static final String PD_TABLE = " cs_pm_purchase_details_v1 pd ON ptdl.id=pd.property_details_id ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pmodified_time desc) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > :start AND offset_ <= :end";

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

		log.info(finalQuery);

		return finalQuery;
	}

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder;
		List<String> relations = criteria.getRelations();

		if (criteria.getRelations() == null) {
			builder = new StringBuilder(SELECT);
			builder.append(PT_ALL + OWNER_ALL + CC_ALL + PD_ALL);
			builder.append(PT_COLUMNS + "," + OWNER_COLUMNS + "," + CC_COLUMNS + "," + PD_COLUMNS);
			builder.append(PT_TABLE + LEFT_JOIN + OWNER_TABLE + LEFT_JOIN + CC_TABLE + LEFT_JOIN + PD_TABLE);
		} else {

			builder = new StringBuilder(SELECT);

			String columns[] = { PT_COLUMNS };
			List<String> columnList = new ArrayList<>(Arrays.asList(columns));

			String tables[] = { PT_TABLE };
			List<String> tableList = new ArrayList<>(Arrays.asList(tables));

			builder.append(PT_ALL);

			if (relations.contains("owner")) {
				builder.append(OWNER_ALL);
			}

			if (relations.contains("court")) {
				builder.append(CC_ALL);
			}

			if (relations.contains("purchase")) {
				builder.append(PD_ALL);
			}

			// columns
			if (relations.contains("owner")) {
				columnList.add(OWNER_COLUMNS);
			}

			if (relations.contains("court")) {
				columnList.add(CC_COLUMNS);
			}

			if (relations.contains("purchase")) {
				columnList.add(PD_COLUMNS);
			}
			String output = columnList.stream().reduce(null, (str1, str2) -> str1 == null ? str2 : str1 + " , " + str2);
			builder.append(output);

			// Joins
			if (relations.contains("owner")) {
				tableList.add(OWNER_TABLE);
			}

			if (relations.contains("court")) {
				tableList.add(CC_TABLE);
			}

			if (relations.contains("purchase")) {
				tableList.add(PD_TABLE);
			}
			String tableOutput = tableList.stream().reduce(null,
					(str1, str2) -> str1 == null ? str2 : str1 + LEFT_JOIN + str2);
			builder.append(tableOutput);
		}

//		TODO: has doubt
//		Search Query for Drafted applications
//		createdBy = currentUserId OR states IN (’STATE1’,’STATE2’,’STATE3’,’STATE4’)

		if (null != criteria.getState()) {
			if (criteria.getState().contains(PSConstants.PM_DRAFTED)) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append("pt.created_by = '" + criteria.getUserId() + "' AND ");
				builder.append("pt.state IN (:state)");
				preparedStmtList.put("state", criteria.getState());
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append("pt.created_by = '" + criteria.getUserId() + "' OR ");
				builder.append("pt.state IN (:state)");
				preparedStmtList.put("state", criteria.getState());
			}
		}

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
			preparedStmtList.put("phone", criteria.getMobileNumber());
		}

//		if (null != criteria.getState()) {
//			addClauseIfRequired(preparedStmtList, builder);
//			builder.append("pt.state = :state");
//			preparedStmtList.put("state", criteria.getState());
//		}

		if (null != criteria.getPropertyId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.id = :id");
			preparedStmtList.put("id", criteria.getPropertyId());
		}
		
		if (null != criteria.getBranchType()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pm_app.branch_type = :branch_type");
			preparedStmtList.put("branch_type", criteria.getBranchType());
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
