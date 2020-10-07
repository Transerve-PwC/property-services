package org.egov.ps.repository;

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

	private static final String PT_COLUMNS = " pt.id as pid, ptdl.branch_type as branch_type, pt.file_number, pt.tenantid as pttenantid, pt.category, pt.sub_category, "
			+ " pt.site_number, pt.sector_number, pt.state as pstate, pt.action as paction, pt.created_by as pcreated_by, pt.created_time as pcreated_time, "
			+ " pt.last_modified_by as pmodified_by, pt.last_modified_time as pmodified_time, "
			+ " pt.property_master_or_allotment_of_site, pt.is_cancelation_of_site, "

			+ " ptdl.id as ptdlid, ptdl.property_id as pdproperty_id, ptdl.property_type as pdproperty_type, "
			+ " ptdl.tenantid as pdtenantid, ptdl.type_of_allocation, ptdl.mode_of_auction, ptdl.scheme_name,ptdl.date_of_auction, "
			+ " ptdl.area_sqft, ptdl.rate_per_sqft, ptdl.last_noc_date, ptdl.service_category, "
			+ " ptdl.is_property_active, ptdl.trade_type, ptdl.company_name, ptdl.company_address, ptdl.company_registration_number, "
			+ " ptdl.company_registration_date, ptdl.decree_date, ptdl.court_details, ptdl.civil_titled_as, ptdl.company_or_firm, "
			+ " ptdl.company_type, ptdl.emd_amount, ptdl.emd_date , ptdl.property_registered_to, ptdl.entity_type ";

	private static final String OWNER_COLUMNS = " ownership.id as oid, ownership.property_details_id as oproperty_details_id, "
			+ " ownership.tenantid as otenantid, ownership.serial_number as oserial_number, "
			+ " ownership.share as oshare, ownership.cp_number as ocp_number, ownership.state as ostate, ownership.action as oaction, "
			+ " ownership.created_by as ocreated_by, ownership.created_time as ocreated_time, ownership.ownership_type, "
			+ " ownership.last_modified_by as omodified_by, ownership.last_modified_time as omodified_time, "

			+ " od.id as odid, od.owner_id as odowner_id,"
			+ " od.owner_name as odowner_name, od.tenantid as odtenantid,"
			+ " od.guardian_name, od.guardian_relation, od.mobile_number,"
			+ " od.allotment_number, od.date_of_allotment, od.possesion_date, od.is_approved, "
			+ " od.is_current_owner, od.is_master_entry, od.address, od.is_director ";

	// + " payment.id as payid, payment.tenantid as paytenantid,
	// payment.property_details_id as payproperty_details_id, "
	// + " payment.payment_type, payment.due_date_of_payment, payment.payable,
	// payment.amount, "
	// + " payment.total, payment.date_of_deposit, payment.delay_in_payment, "
	// + " payment.interest_for_delay, payment.total_amount_due_with_interest,
	// payment.amount_deposited, "
	// + " payment.amount_deposited_intt, payment.balance, payment.balance_intt, "
	// + " payment.total_due, payment.receipt_number, payment.receipt_date, "
	// + " payment.st_rate_of_st_gst, payment.st_amount_of_gst,
	// payment.st_payment_made_by, "
	// + " payment.bank_name, payment.cheque_number, "
	// + " payment.installment_one, payment.installment_two,
	// payment.installment_two_due_date, payment.installment_three,
	// payment.installment_three_due_date, "
	// + " payment.monthly_or_annually, payment.ground_rent_start_date,
	// payment.rent_revision, "
	// + " payment.lease_period, payment.license_fee_of_year, payment.license_fee,
	// payment.security_amount, payment.security_date, "
	// + " payment.created_by as paycreated_by, payment.created_time as
	// paycreated_time, payment.last_modified_by as paymodified_by,
	// payment.last_modified_time as paymodified_time ";

	private static final String CC_COLUMNS = " cc.id as ccid, cc.property_details_id as ccproperty_details_id,"
			+ " cc.tenantid as cctenantid, cc.estate_officer_court as ccestate_officer_court,"
			+ " cc.commissioners_court as cccommissioners_court, cc.chief_administartors_court as ccchief_administartors_court, cc.advisor_to_admin_court as ccadvisor_to_admin_court, cc.honorable_district_court as cchonorable_district_court,"
			+ " cc.honorable_high_court as cchonorable_high_court, cc.honorable_supreme_court as cchonorable_supreme_court,"
			+ " cc.created_by as cccreated_by, cc.created_time as cccreated_time, cc.last_modified_by as ccmodified_by, cc.last_modified_time as ccmodified_time ";

	private static final String PT_TABLE = " FROM cs_ep_property_v1 pt " + INNER_JOIN
			+ " cs_ep_property_details_v1 ptdl  ON pt.id =ptdl.property_id ";

	private static final String OWNER_TABLE = " cs_ep_owner_v1 ownership " + LEFT_JOIN
			+ " cs_ep_owner_details_v1 od ON ownership.id = od.owner_id ";

	// + LEFT_JOIN
	// + " cs_ep_payment_v1 payment ON ptdl.id=payment.property_details_id ";

	private static final String CC_TABLE = " cs_ep_court_case_v1 cc ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pmodified_time desc) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > :start AND offset_ <= :end";

	public static final String RELATION_OWNER = "owner";
	public static final String RELATION_OWNER_DOCUMENTS = "ownerdocs";
	public static final String RELATION_COURT = "court";

	private String addPaginationWrapper(String query, Map<String, Object> preparedStmtList, PropertyCriteria criteria) {

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

		log.debug(finalQuery);

		return finalQuery;
	}

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(SELECT);

		builder.append(PT_COLUMNS);

		builder.append(PT_TABLE);

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
			builder.append("pt.file_number=:fileNumber");
			preparedStmtList.put("fileNumber", criteria.getFileNumber());
		}

		if (null != criteria.getCategory()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.category = :category");
			preparedStmtList.put("category", criteria.getCategory());
		}

		if (null != criteria.getPropertyId()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.id = :id");
			preparedStmtList.put("id", criteria.getPropertyId());
		}

		if (null != criteria.getBranchType()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("ptdl.branch_type = :branch_type");
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

	private static final String OWNER_DOCS_COLUMNS = " doc.id as docid, doc.reference_id as docreference_id, doc.tenantid as doctenantid,"
			+ " doc.is_active as docis_active, doc.document_type, doc.file_store_id, doc.property_id as docproperty_id,"
			+ " doc.created_by as dcreated_by, doc.created_time as dcreated_time, doc.last_modified_by as dmodified_by, doc.last_modified_time as dmodified_time ";

	public String getOwnerDocsQuery(List<String> ownerDetailIds, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(SELECT);
		sb.append(OWNER_DOCS_COLUMNS);
		sb.append(" FROM cs_ep_documents_v1 doc ");
		sb.append(" where doc.reference_id IN (:references)");
		params.put("references", ownerDetailIds);
		return sb.toString();
	}

	public String getOwnersQuery(List<String> propertyDetailIds, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(SELECT);
		sb.append(OWNER_COLUMNS);
		sb.append(" FROM " + OWNER_TABLE);
		sb.append(" where ownership.property_details_id IN (:propertyDetailIds)");
		params.put("propertyDetailIds", propertyDetailIds);
		return sb.toString();
	}
	
	public String getCourtCasesQuery(List<String> propertyDetailIds, Map<String, Object> params) {
		StringBuilder sb = new StringBuilder(SELECT);
		sb.append(CC_COLUMNS);
		sb.append(" FROM " + CC_TABLE);
		sb.append(" where cc.property_details_id IN (:propertyDetailIds)");
		params.put("propertyDetailIds", propertyDetailIds);
		return sb.toString();
	}
}
