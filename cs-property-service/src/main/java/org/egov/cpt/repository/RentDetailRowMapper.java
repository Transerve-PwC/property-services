package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.cpt.models.Address;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Document;
import org.egov.cpt.models.MortgageApprovedGrantDetails;
import org.egov.cpt.models.NoticeGeneration;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyDetails;
import org.egov.cpt.models.PropertyImages;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RentDetailRowMapper implements ResultSetExtractor<List<Property>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

		while (rs.next()) {
			String propertyId = rs.getString("pid");
			Property currentProperty = propertyMap.get(propertyId);
			String tenantId = rs.getString("pttenantid");

			if (null == currentProperty) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
						.createdTime(rs.getLong("pcreated_date")).lastModifiedBy(rs.getString("pmodified_by"))
						.lastModifiedTime(rs.getLong("pmodified_date")).build();
				
				currentProperty = Property.builder().id(propertyId).transitNumber(rs.getString("transit_number"))
						.tenantId(tenantId).colony(rs.getString("colony"))
						.masterDataState(rs.getString("master_data_state"))
						.masterDataAction(rs.getString("master_data_action"))
						.auditDetails(auditdetails).build();
				propertyMap.put(propertyId, currentProperty);
			}
			addChildrenToProperty(rs, currentProperty);
		}
		return new ArrayList<>(propertyMap.values());
	}

	private void addChildrenToProperty(ResultSet rs, Property property) throws SQLException {

//Demands
			if (rs.getString("demand_id") != null) {
				AuditDetails demandAuditDetails = AuditDetails.builder().createdBy(rs.getString("demand_created_by"))
						.createdTime(rs.getLong("demand_created_date")).lastModifiedBy(rs.getString("demand_modified_by"))
						.lastModifiedTime(rs.getLong("demand_modified_date")).build();
				RentDemand rentDemand = RentDemand.builder().id(rs.getString("demand_id"))
						.propertyId("demand_pid")
						.initialGracePeriod(rs.getInt("demand_IniGracePeriod"))
						.generationDate(rs.getLong("demand_genDate"))
						.collectionPrincipal(rs.getDouble("demand_colPrincipal"))
						.remainingPrincipal(rs.getDouble("demand_remPrincipal"))
						.interestSince(rs.getLong("demand_intSince"))
						.mode(RentDemand.ModeEnum.fromValue(rs.getString("demand_mode")))
						.auditDetails(demandAuditDetails)
						.build();
				property.addDemandItem(rentDemand);
			}
			
	//Payments
			if (rs.getString("payment_id") != null) {
				AuditDetails paymentAuditDetails = AuditDetails.builder().createdBy(rs.getString("payment_created_by"))
						.createdTime(rs.getLong("payment_created_date")).lastModifiedBy(rs.getString("payment_modified_by"))
						.lastModifiedTime(rs.getLong("payment_modified_date")).build();
				RentPayment rentPayment = RentPayment.builder().id(rs.getString("payment_id"))
						.propertyId("payment_pid")
						.receiptNo(rs.getString("payment_receiptNo"))
						.amountPaid(rs.getDouble("payment_amtPaid"))
						.dateOfPayment(rs.getLong("payment_dateOfPayment"))
						.mode(RentPayment.ModeEnum.fromValue(rs.getString("payment_mode")))
						.auditDetails(paymentAuditDetails)
						.build();
				property.addPaymentItem(rentPayment);
			}
			
	//Account
			if (rs.getString("account_id") != null) {
				AuditDetails accountAuditDetails = AuditDetails.builder().createdBy(rs.getString("account_created_by"))
						.createdTime(rs.getLong("account_created_date")).lastModifiedBy(rs.getString("account_modified_by"))
						.lastModifiedTime(rs.getLong("account_modified_date")).build();
				RentAccount rentAccount = RentAccount.builder().id(rs.getString("account_id"))
						.propertyId("account_pid")
						.remainingAmount(rs.getDouble("account_remainingAmount"))
						.tenantId(rs.getString("account_tenantid"))
						.auditDetails(accountAuditDetails)
						.build();
				property.setRentAccount(rentAccount);
			}
			
	//Collection
			if (rs.getString("collection_id") != null) {
				AuditDetails collectionAuditDetails = AuditDetails.builder().createdBy(rs.getString("collection_created_by"))
						.createdTime(rs.getLong("collection_created_date")).lastModifiedBy(rs.getString("collection_modified_by"))
						.lastModifiedTime(rs.getLong("collection_modified_date")).build();
				RentCollection rentCollection = RentCollection.builder().id(rs.getString("collection_id"))
						.paymentId(rs.getString("collection_payment_id"))
						.demandId(rs.getString("collection_demand_id"))
						.interestCollected(rs.getDouble("collection_intCollected"))
						.principalCollected(rs.getDouble("collection_principalCollected"))
						.tenantId(rs.getString("collection_tenantid"))
						.auditDetails(collectionAuditDetails)
						.build();
				property.addCollectionItem(rentCollection);
			}
		}
	
}
