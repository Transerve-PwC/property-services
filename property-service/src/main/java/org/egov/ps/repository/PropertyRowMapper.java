package org.egov.ps.repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Document;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Payment;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.PurchaseDetails;
import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PropertyRowMapper implements ResultSetExtractor<List<Property>> {

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

		while (rs.next()) {

			Property currentProperty = null;

			if (hasColumn(rs, "pid")) {
				String propertyId = rs.getString("pid");
				currentProperty = propertyMap.get(propertyId);
				String tenantId = rs.getString("pttenantid");
				String propertyDetailId = rs.getString("ptdlid");

				if (null == currentProperty) {
					AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
							.createdTime(rs.getLong("pcreated_time")).lastModifiedBy(rs.getString("pmodified_by"))
							.lastModifiedTime(rs.getLong("pmodified_time")).build();

					AuditDetails pdAuditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
							.createdTime(rs.getLong("pcreated_time")).lastModifiedBy(rs.getString("pmodified_by"))
							.lastModifiedTime(rs.getLong("pmodified_time")).build();

					PropertyDetails propertyDetails = PropertyDetails.builder().id(propertyDetailId)
							.propertyId(rs.getString("pdproperty_id")).propertyType(rs.getString("pdproperty_type"))
							.tenantId(tenantId).typeOfAllocation(rs.getString("type_of_allocation"))
							.modeOfAuction(rs.getString("mode_of_auction")).schemeName(rs.getString("scheme_name"))
							.areaSqft(rs.getString("area_sqft")).dateOfAuction(rs.getLong("date_of_auction"))
							.ratePerSqft(rs.getString("rate_per_sqft")).lastNocDate(rs.getLong("last_noc_date"))
							.serviceCategory(rs.getString("service_category")).auditDetails(pdAuditdetails).build();

					currentProperty = Property.builder().id(propertyId).fileNumber(rs.getString("file_number"))
							.tenantId(tenantId).category(rs.getString("category"))
							.subCategory(rs.getString("sub_category")).sectorNumber(rs.getString("sector_number"))
							.siteNumber(rs.getString("site_number")).state(rs.getString("state"))
							.action(rs.getString("action")).propertyDetails(propertyDetails).auditDetails(auditdetails)
							.build();
					propertyMap.put(propertyId, currentProperty);
				}
			}

			addChildrenToProperty(rs, currentProperty, propertyMap);
		}

		return new ArrayList<>(propertyMap.values());

	}

	private void addChildrenToProperty(ResultSet rs, Property property, LinkedHashMap<String, Property> propertyMap)
			throws SQLException {

		if (hasColumn(rs, "oid")) {
			String ownerId = rs.getString("oid");
			String ownerDetailId = rs.getString("odid");
			String OwnerPropertyDetailId = rs.getString("oproperty_details_id");

			if (ownerId != null) {

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("ocreated_by"))
						.createdTime(rs.getLong("ocreated_time")).lastModifiedBy(rs.getString("omodified_by"))
						.lastModifiedTime(rs.getLong("omodified_time")).build();

				OwnerDetails ownerDetails = OwnerDetails.builder().id(ownerDetailId).ownerId(rs.getString("odowner_id"))
						.ownerName(rs.getString("odowner_name")).tenantId(rs.getString("otenantid"))
						.guardianName(rs.getString("guardian_name")).guardianRelation(rs.getString("guardian_relation"))
						.mobileNumber(rs.getString("mobile_number")).allotmentNumber(rs.getString("allotment_number"))
						.dateOfAllotment(rs.getLong("date_of_allotment")).possesionDate(rs.getLong("possesion_date"))
						.isCurrentOwner(rs.getBoolean("is_current_owner"))
						.isMasterEntry(rs.getBoolean("is_master_entry")).dueAmount(rs.getBigDecimal("due_amount"))
						.address(rs.getString("address")).auditDetails(auditdetails).build();

				Owner owners = Owner.builder().id(ownerId).propertyDetailsId(OwnerPropertyDetailId)
						.tenantId(rs.getString("otenantid")).serialNumber(rs.getString("oserial_number"))
						.share(rs.getDouble("oshare")).cpNumber(rs.getString("ocp_number"))
						.state(rs.getString("ostate")).action(rs.getString("oaction")).ownerDetails(ownerDetails)
						.auditDetails(auditdetails).build();

				if (hasColumn(rs, "pid")) {
					property.getPropertyDetails().addOwnerItem(owners);
				} else {
					Property property2 = new Property();
					PropertyDetails propertyDetails = new PropertyDetails();
					propertyDetails.addOwnerItem(owners);
					property2.setPropertyDetails(propertyDetails);

					propertyMap.put(ownerId, property2);
				}

			}
		}

		if (hasColumn(rs, "docid")) {
			String docOwnerDetailId = rs.getString("docowner_details_id");
			List<Owner> owners = property.getPropertyDetails().getOwners();
			if (!CollectionUtils.isEmpty(owners)) {
				owners.forEach(owner -> {
					try {
						if (rs.getString("docid") != null && rs.getBoolean("docis_active")
								&& docOwnerDetailId.equals(owner.getOwnerDetails().getId())) {

							AuditDetails docAuditdetails = AuditDetails.builder().createdBy(rs.getString("dcreated_by"))
									.createdTime(rs.getLong("dcreated_time"))
									.lastModifiedBy(rs.getString("dmodified_by"))
									.lastModifiedTime(rs.getLong("dmodified_time")).build();

							Document ownerDocument = Document.builder().id(rs.getString("docid"))
									.referenceId(rs.getString("docowner_details_id"))
									.tenantId(rs.getString("doctenantid")).isActive(rs.getBoolean("docis_active"))
									.documentType(rs.getString("document_type"))
									.fileStoreId(rs.getString("file_store_id"))
									.propertyId(rs.getString("docproperty_id")).auditDetails(docAuditdetails).build();
							owner.getOwnerDetails().addOwnerDocumentsItem(ownerDocument);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}
		}

		if (hasColumn(rs, "payid")) {
			String payId = rs.getString("payid");
			String payTenentId = rs.getString("paytenantid");
			String payOwnerDetailId = rs.getString("payowner_details_id");
			List<Owner> owners = property.getPropertyDetails().getOwners();
			if (!CollectionUtils.isEmpty(owners)) {
				owners.forEach(owner -> {
					try {
						if (payId != null && payOwnerDetailId.equals(owner.getOwnerDetails().getId())) {

							AuditDetails payAuditdetails = AuditDetails.builder()
									.createdBy(rs.getString("paycreated_by")).createdTime(rs.getLong("paycreated_time"))
									.lastModifiedBy(rs.getString("paymodified_by"))
									.lastModifiedTime(rs.getLong("paymodified_time")).build();

							Payment paymentItem = Payment.builder().id(payId).tenantId(payTenentId)
									.ownerDetailsId(payOwnerDetailId)
									.grDueDateOfPayment(rs.getLong("gr_due_date_of_payment"))
									.grPayable(rs.getBigDecimal("gr_payable"))
									.grAmountOfGr(rs.getBigDecimal("gr_amount_of_gr"))
									.grTotalGr(rs.getBigDecimal("gr_total_gr"))
									.grDateOfDeposit(rs.getLong("gr_date_of_deposit"))
									.grDelayInPayment(rs.getBigDecimal("gr_delay_in_payment"))
									.grInterestForDelay(rs.getBigDecimal("gr_interest_for_delay"))
									.grTotalAmountDueWithInterest(rs.getBigDecimal("gr_total_amount_due_with_interest"))
									.grAmountDepositedGr(rs.getBigDecimal("gr_amount_deposited_gr"))
									.grAmountDepositedIntt(rs.getBigDecimal("gr_amount_deposited_intt"))
									.grBalanceGr(rs.getBigDecimal("gr_balance_gr"))
									.grBalanceIntt(rs.getBigDecimal("gr_balance_intt"))
									.grTotalDue(rs.getBigDecimal("gr_total_due"))
									.grReceiptNumber(rs.getString("gr_receipt_number"))
									.grReceiptDate(rs.getLong("gr_receipt_date"))
									.stRateOfStGst(rs.getBigDecimal("st_rate_of_st_gst"))
									.stAmountOfGst(rs.getBigDecimal("st_amount_of_gst"))
									.stAmountDue(rs.getBigDecimal("st_amount_due"))
									.stDateOfDeposit(rs.getLong("st_date_of_deposit"))
									.stDelayInPayment(rs.getBigDecimal("st_delay_in_payment"))
									.stInterestForDelay(rs.getBigDecimal("st_interest_for_delay"))
									.stTotalAmountDueWithInterest(rs.getBigDecimal("st_total_amount_due_with_interest"))
									.stAmountDepositedStGst(rs.getBigDecimal("st_amount_deposited_st_gst"))
									.stAmountDepositedIntt(rs.getBigDecimal("st_amount_deposited_intt"))
									.stBalanceStGst(rs.getBigDecimal("st_balance_st_gst"))
									.stBalanceIntt(rs.getBigDecimal("st_balance_intt"))
									.stTotalDue(rs.getBigDecimal("st_total_due"))
									.stReceiptNumber(rs.getString("st_receipt_number"))
									.stReceiptDate(rs.getLong("st_receipt_date"))
									.stPaymentMadeBy(rs.getString("st_payment_made_by")).auditDetails(payAuditdetails)
									.build();
							owner.getOwnerDetails().addPaymentItem(paymentItem);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}
		}

		if (hasColumn(rs, "ccid")) {
			String courtCasePropertDetailId = rs.getString("ccproperty_details_id");

			if (courtCasePropertDetailId != null
					&& courtCasePropertDetailId.equals(property.getPropertyDetails().getId())) {

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("cccreated_by"))
						.createdTime(rs.getLong("cccreated_time")).lastModifiedBy(rs.getString("ccmodified_by"))
						.lastModifiedTime(rs.getLong("ccmodified_time")).build();

				CourtCase courtCase = CourtCase.builder().id(rs.getString("ccid"))
						.propertyDetailsId(courtCasePropertDetailId).tenantId(rs.getString("cctenantid"))
						.estateOfficerCourt(rs.getString("ccestate_officer_court"))
						.commissionersCourt(rs.getString("cccommissioners_court"))
						.chiefAdministartorsCourt(rs.getString("ccchief_administartors_court"))
						.advisorToAdminCourt(rs.getString("ccadvisor_to_admin_court"))
						.honorableDistrictCourt(rs.getString("cchonorable_district_court"))
						.honorableHighCourt(rs.getString("cchonorable_high_court"))
						.honorableSupremeCourt(rs.getString("cchonorable_supreme_court")).auditDetails(auditdetails)
						.build();

				property.getPropertyDetails().addCourtCaseItem(courtCase);

			}
		}

		if (hasColumn(rs, "pdid")) {
			String purchaseDetailPropertyDetailId = rs.getString("pdproperty_details_id");
			if (purchaseDetailPropertyDetailId != null
					&& purchaseDetailPropertyDetailId.equals(property.getPropertyDetails().getId())) {

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pdcreated_by"))
						.createdTime(rs.getLong("pdcreated_time")).lastModifiedBy(rs.getString("pdmodified_by"))
						.lastModifiedTime(rs.getLong("pdmodified_time")).build();

				PurchaseDetails purchaseDetails = PurchaseDetails.builder().id(rs.getString("pdid"))
						.propertyDetailsId(purchaseDetailPropertyDetailId).tenantId(rs.getString("pdtenantid"))
						.newOwnerName(rs.getString("pdnew_owner_name"))
						.newOwnerFatherName(rs.getString("pdnew_owner_father_name"))
						.newOwnerAddress(rs.getString("pdnew_owner_address"))
						.newOwnerMobileNumber(rs.getString("pdnew_owner_mobile_number"))
						.sellerName(rs.getString("pdseller_name"))
						.sellerFatherName(rs.getString("pdseller_father_name"))
						.percentageOfShare(rs.getString("pdpercentage_of_share"))
						.modeOfTransfer(rs.getString("pdmode_of_transfer"))
						.registrationNumber(rs.getString("pdregistration_number"))
						.dateOfRegistration(rs.getLong("pddate_of_registration")).auditDetails(auditdetails).build();

				property.getPropertyDetails().addPurchaseDetailsItem(purchaseDetails);

			}
		}

	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}

}
