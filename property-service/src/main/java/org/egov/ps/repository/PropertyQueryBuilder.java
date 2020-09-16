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
	private static final String OWNER_ALL = " ownership.*, od.*, doc.*, "; //payment.*,
	private static final String CC_ALL = " cc.*, ";

	private static final String PT_COLUMNS = " pt.id as pid, pm_app.branch_type as branch_type, pt.file_number, pt.tenantid as pttenantid, pt.category, pt.sub_category, "
			+ " pt.site_number, pt.sector_number, pt.state as pstate, pt.action as paction, pt.created_by as pcreated_by, pt.created_time as pcreated_time, "
			+ " pt.last_modified_by as pmodified_by, pt.last_modified_time as pmodified_time,"

			+ " ptdl.id as ptdlid, ptdl.property_id as pdproperty_id, ptdl.property_type as pdproperty_type, "
			+ " ptdl.tenantid as pdtenantid, ptdl.type_of_allocation, ptdl.mode_of_auction, ptdl.scheme_name,ptdl.date_of_auction, "
			+ " ptdl.area_sqft, ptdl.rate_per_sqft, ptdl.last_noc_date, ptdl.service_category, "
			+ " ptdl.is_property_active, ptdl.trade_type, ptdl.company_name, ptdl.company_address, ptdl.company_registration_number, "
			+ " ptdl.company_type, ptdl.emd_amount, ptdl.emd_date ";

	private static final String OWNER_COLUMNS = " ownership.id as oid, ownership.property_details_id as oproperty_details_id, "
			+ " ownership.tenantid as otenantid, ownership.serial_number as oserial_number, "
			+ " ownership.share as oshare, ownership.cp_number as ocp_number, ownership.state as ostate, ownership.action as oaction, "
			+ " ownership.created_by as ocreated_by, ownership.created_time as ocreated_time, "
			+ " ownership.last_modified_by as omodified_by, ownership.last_modified_time as omodified_time, "

			+ " od.id as odid, od.owner_id as odowner_id,"
			+ " od.owner_name as odowner_name, od.tenantid as odtenantid,"
			+ " od.guardian_name, od.guardian_relation, od.mobile_number,"
			+ " od.allotment_number, od.date_of_allotment, od.possesion_date, od.is_approved, "
			+ " od.is_current_owner, od.is_master_entry, od.address, "

			+ " doc.id as docid, doc.reference_id as docowner_details_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.file_store_id, doc.property_id as docproperty_id,"
			+ " doc.created_by as dcreated_by, doc.created_time as dcreated_time, doc.last_modified_by as dmodified_by, doc.last_modified_time as dmodified_time ";

//			+ " payment.id as payid, payment.tenantid as paytenantid, payment.owner_details_id as payowner_details_id, "
//			+ " payment.payment_type, payment.due_date_of_payment, payment.payable, payment.amount, "
//			+ " payment.total, payment.date_of_deposit, payment.delay_in_payment, "
//			+ " payment.interest_for_delay, payment.total_amount_due_with_interest, payment.amount_deposited, "
//			+ " payment.amount_deposited_intt, payment.balance, payment.balance_intt, "
//			+ " payment.total_due, payment.receipt_number, payment.receipt_date, "
//			+ " payment.st_rate_of_st_gst, payment.st_amount_of_gst, payment.st_payment_made_by, "
//			+ " payment.bank_name, payment.cheque_number, " 
//			+ " payment.installment_one, payment.installment_two, payment.installment_two_due_date, payment.installment_three, payment.installment_three_due_date, "
//			+ " payment.monthly_or_annually, payment.ground_rent_start_date, payment.rent_revision, "
//			+ " payment.lease_period, payment.license_fee_of_year, payment.license_fee, payment.security_amount, payment.security_date, "
//			+ " payment.created_by as paycreated_by, payment.created_time as paycreated_time, payment.last_modified_by as paymodified_by, payment.last_modified_time as paymodified_time ";

	private static final String CC_COLUMNS = " cc.id as ccid, cc.owner_details_id as ccproperty_details_id,"
			+ " cc.tenantid as cctenantid, cc.estate_officer_court as ccestate_officer_court,"
			+ " cc.commissioners_court as cccommissioners_court, cc.chief_administartors_court as ccchief_administartors_court, cc.advisor_to_admin_court as ccadvisor_to_admin_court, cc.honorable_district_court as cchonorable_district_court,"
			+ " cc.honorable_high_court as cchonorable_high_court, cc.honorable_supreme_court as cchonorable_supreme_court,"
			+ " cc.created_by as cccreated_by, cc.created_time as cccreated_time, cc.last_modified_by as ccmodified_by, cc.last_modified_time as ccmodified_time ";

	private static final String PT_TABLE = " FROM cs_ep_property_v1 pt " + INNER_JOIN
			+ " cs_ep_property_details_v1 ptdl  ON pt.id =ptdl.property_id "+ LEFT_JOIN
			+ " cs_ep_application_v1 pm_app ON pt.id= pm_app.property_id ";

	private static final String OWNER_TABLE = " cs_ep_owner_v1 ownership  ON ptdl.id=ownership.property_details_id "
			+ LEFT_JOIN + " cs_ep_owner_details_v1 od ON ownership.id = od.owner_id " + LEFT_JOIN
			+ " cs_ep_documents_v1 doc ON od.id=doc.reference_id " ;
//	        + LEFT_JOIN
//			+ " cs_ep_payment_v1 payment ON od.id=payment.owner_details_id ";

	private static final String CC_TABLE = " cs_ep_court_case_v1 cc ON od.id=cc.owner_details_id ";

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
			builder.append(PT_ALL + OWNER_ALL + CC_ALL);
			builder.append(PT_COLUMNS + "," + OWNER_COLUMNS + "," + CC_COLUMNS);
			builder.append(PT_TABLE + LEFT_JOIN + OWNER_TABLE + LEFT_JOIN + CC_TABLE);
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

			// columns
			if (relations.contains("owner")) {
				columnList.add(OWNER_COLUMNS);
			}

			if (relations.contains("court")) {
				columnList.add(CC_COLUMNS);
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
