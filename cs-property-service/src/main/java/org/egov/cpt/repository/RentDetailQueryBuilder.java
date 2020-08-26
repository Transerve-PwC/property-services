package org.egov.cpt.repository;

import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.PropertyCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class RentDetailQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY pmodified_date desc) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > :start AND offset_ <= :end";

	private static final String RENTDETAIL_SEARCH_QUERY = SELECT + "pt.*, demand.*,payment.*,account.*,collection.*,"
			+ " pt.id as pid, pt.transit_number, pt.tenantid as pttenantid, pt.colony, pt.master_data_state, pt.master_data_action,"
			+ " pt.created_by as pcreated_by, pt.created_date as pcreated_date, pt.modified_by as pmodified_by, pt.modified_date as pmodified_date,"
			
			+ "demand.id as demand_id,demand.property_id as demand_pid,demand.initialGracePeriod as demand_IniGracePeriod, demand.generationDate as demand_genDate,"
			+ "demand.collectionPrincipal as demand_colPrincipal,demand.remainingPrincipal as demand_remPrincipal, demand.interestSince as demand_intSince,"
			+ "demand.mode as demand_mode, demand.created_by as demand_created_by, demand.created_date as demand_created_date,"
			+ "demand.modified_by as demand_modified_by,demand.modified_date as demand_modified_date,"

			+ "payment.id as payment_id, payment.property_id as payment_pid,payment.receiptNo as payment_receiptNo,payment.amountPaid as payment_amtPaid,"
			+ "payment.dateOfPayment as payment_dateOfPayment,payment.mode as payment_mode,payment.created_by as payment_created_by, payment.created_date as payment_created_date,"
			+ "payment.modified_by as payment_modified_by,payment.modified_date as payment_modified_date,"

			+ "account.id as account_id,account.property_id as account_pid,account.remainingAmount as account_remainingAmount,account.tenantid as account_tenantid,"
			+ "account.created_by as account_created_by, account.created_date as account_created_date,"
			+ "account.modified_by as account_modified_by,account.modified_date as account_modified_date,"

			+ "collection.id as collection_id ,collection.demand_id as collection_demand_id,collection.payment_id as collection_payment_id,"
			+ "collection.interestCollected as collection_intCollected,collection.principalCollected as collection_principalCollected,collection.tenantid as collection_tenantid,"
			+ "collection.created_by as collection_created_by, collection.created_date as collection_created_date,"
			+ "collection.modified_by as collection_modified_by,collection.modified_date as collection_modified_date "
			
			+ " FROM  cs_pt_property_v1 pt "+INNER_JOIN
			+ " cs_pt_demand demand ON pt.id=demand.property_id " + LEFT_JOIN
			+ " cs_pt_payment payment ON pt.id=payment.property_id " + LEFT_JOIN
			+ " cs_pt_account account ON pt.id=account.property_id " + LEFT_JOIN
			+ " cs_pt_collection collection ON demand.id=collection.demand_id" + " and "
			+ " payment.id=collection.payment_id";

	private String addPaginationWrapper(String query,  Map<String, Object> preparedStmtList,
			PropertyCriteria criteria) {

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

		preparedStmtList.put("start",offset);
		preparedStmtList.put("end",limit + offset);

		return finalQuery;
	}

	private static void addClauseIfRequired(Map<String, Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}

	public String getPropertyRentSearchQuery(PropertyCriteria criteria, Map<String, Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(RENTDETAIL_SEARCH_QUERY);

		if (!ObjectUtils.isEmpty(criteria.getPropertyId())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("demand.property_id=:propId");
			preparedStmtList.put("propId",criteria.getPropertyId());
		}

		if (null != criteria.getTransitNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("pt.transit_number=:trnsNumber");
			preparedStmtList.put("trnsNumber",criteria.getTransitNumber());
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}
}
