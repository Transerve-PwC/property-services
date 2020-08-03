package org.egov.ps.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.OwnerDocument;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.PurchaseDetails;
import org.egov.ps.web.contracts.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class PropertyRowMapper implements ResultSetExtractor<List<Property>> {

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

		while (rs.next()) {
			String propertyId = rs.getString("pid");
			Property currentProperty = propertyMap.get(propertyId);
			String tenantId = rs.getString("pttenantid");

			if (null == currentProperty) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
						.createdTime(rs.getLong("pcreated_time")).lastModifiedBy(rs.getString("pmodified_by"))
						.lastModifiedTime(rs.getLong("pmodified_time")).build();

				currentProperty = Property.builder().id(propertyId).fileNumber(rs.getString("file_number"))
						.tenantId(tenantId).category(rs.getString("category")).subCategory(rs.getString("sub_category"))
						.sectorNumber(rs.getString("sector_number")).siteNumber(rs.getString("site_number"))
						.state(rs.getString("state")).action(rs.getString("action")).auditDetails(auditdetails).build();
				propertyMap.put(propertyId, currentProperty);
			}
			addChildrenToProperty(rs, currentProperty);
		}
		return new ArrayList<>(propertyMap.values());
	}

	private void addChildrenToProperty(ResultSet rs, Property property) throws SQLException {

		String tenantId = property.getTenantId();
		String propertyDetailId = rs.getString("pdid");
		String ownerId = rs.getString("oid");
		String ownerDetailId = rs.getString("odid");
		String courtCasePropertDetailId = rs.getString("ccproperty_details_id");
		String purchaseDetailPropertyDetailId = rs.getString("pdproperty_details_id");

		if (property.getPropertyDetails() == null) {

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
					.createdTime(rs.getLong("pcreated_time")).lastModifiedBy(rs.getString("pmodified_by"))
					.lastModifiedTime(rs.getLong("pmodified_time")).build();

//			AuditDetails ccauditdetails = AuditDetails.builder().createdBy(rs.getString("cccreated_by"))
//					.createdTime(rs.getLong("cccreated_time")).lastModifiedBy(rs.getString("ccmodified_by"))
//					.lastModifiedTime(rs.getLong("ccmodified_time")).build();
//			
//			AuditDetails pdauditdetails = AuditDetails.builder().createdBy(rs.getString("pdcreated_by"))
//					.createdTime(rs.getLong("pdcreated_time")).lastModifiedBy(rs.getString("pdmodified_by"))
//					.lastModifiedTime(rs.getLong("pdmodified_time")).build();

//			PurchaseDetails purchaseDetails = PurchaseDetails.builder().id(rs.getString("pdid")).propertyDetailsId(purchaseDetailPropertyDetailId)
//					.tenantId(rs.getString("pdtenantid")).newOwnerName(rs.getString("pdnew_owner_name"))
//					.newOwnerFatherName(rs.getBoolean("pdnew_owner_father_name")).newOwnerAddress(rs.getString("pdnew_owner_address"))
//					.newOwnerMobileNumber(rs.getString("pdnew_owner_mobile_number")).sellerName(rs.getString("pdseller_name"))
//					.sellerFatherName(rs.getString("pdseller_father_name")).percentageOfShare(rs.getString("pdpercentage_of_share"))
//					.modeOfTransfer(rs.getString("pdmode_of_transfer")).registrationNumber(rs.getString("pdregistration_number"))
//					.dateOfRegistration(rs.getLong("pddate_of_registration")).auditDetails(pdauditdetails)
//					.build();
//			
//			property.getPropertyDetails().addPurchaseDetailsItem(purchaseDetails);

//			CourtCase courtCase = CourtCase.builder().id(rs.getString("ccid")).propertyDetailsId(courtCasePropertDetailId)
//					.tenantId(rs.getString("cctenantid")).estateOfficerCourt(rs.getString("ccestate_officer_court"))
//					.commissionersCourt(rs.getBoolean("cccommissioners_court"))
//					.chiefAdministartorsCourt(rs.getString("ccchief_administartors_court")).advisorToAdminCourt(rs.getString("ccadvisor_to_admin_court"))
//					.honorableDistrictCourt(rs.getString("cchonorable_district_court")).honorableHighCourt(rs.getString("cchonorable_high_court"))
//					.honorableSupremeCourt(rs.getString("cchonorable_supreme_court")).auditDetails(ccauditdetails)
//					.build();
//			
//			property.getPropertyDetails().addCourtCaseItem(courtCase);

			PropertyDetails propertyDetails = PropertyDetails.builder().id(propertyDetailId)
					.propertyId(rs.getString("pdproperty_id")).propertyType(rs.getString("pdproperty_type"))
					.tenantId(tenantId).typeOfAllocation(rs.getString("type_of_allocation"))
					.modeOfAuction(rs.getString("mode_of_auction")).schemeName(rs.getString("scheme_name"))
					.areaSqft(rs.getString("area_sqft")).dateOfAuction(rs.getLong("date_of_auction"))
					.ratePerSqft(rs.getString("rate_per_sqft")).lastNocDate(rs.getLong("last_noc_date"))
					.serviceCategory(rs.getString("service_category")).auditDetails(auditdetails).build();

//			.purchaseDetails(purchaseDetails).courtCases(courtCase)

//			PurchaseDetails pDetails = propertyDetails.getPurchaseDetails().stream().filter(pd -> {
//				try {
//					return pd.getPropertyDetailsId().equalsIgnoreCase(propertyDetailId);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				return false;
//			}).findFirst().get();
//			pDetails.addPurchaseDetailsItem(purchaseDetails);

			property.setPropertyDetails(propertyDetails);
		}

		String OwnerPropertyDetailId = rs.getString("oproperty_details_id");

//		property.getPropertyDetails().getOwners().forEach(owner -> {
		try {
			if (ownerId != null && OwnerPropertyDetailId.equals(property.getPropertyDetails().getId())) {

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("ocreated_by"))
						.createdTime(rs.getLong("ocreated_time")).lastModifiedBy(rs.getString("omodified_by"))
						.lastModifiedTime(rs.getLong("omodified_time")).build();

				OwnerDetails ownerDetails = OwnerDetails.builder().id(ownerDetailId).ownerId(rs.getString("odowner_id"))
						.ownerName(rs.getString("odowner_name")).tenantId(rs.getString("otenantid"))
						.guardianName(rs.getString("guardian_name")).guardianRelation(rs.getString("guardian_relation"))
						.mobileNumber(rs.getString("mobile_number")).allotmentNumber(rs.getString("allotment_number"))
						.dateOfAllotment(rs.getLong("date_of_allotment")).possesionDate(rs.getLong("possesion_date"))
						.isCurrentOwner(rs.getBoolean("is_current_owner"))
						.isMasterEntry(rs.getBoolean("is_master_entry")).dueAmount(rs.getString("due_amount"))
						.address(rs.getString("address")).auditDetails(auditdetails).build();

				Owner owners = Owner.builder().id(ownerId).propertyDetailsId(propertyDetailId)
						.tenantId(rs.getString("otenantid")).serialNumber(rs.getString("oserial_number"))
						.share(rs.getBoolean("oshare")).cpNumber(rs.getString("ocp_number"))
						.state(rs.getString("ostate")).action(rs.getString("oaction")).ownerDetails(ownerDetails)
						.auditDetails(auditdetails).build();

				property.getPropertyDetails().addOwnerItem(owners);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		});
		
		String docOwnerDetailId = rs.getString("docowner_details_id");
		property.getPropertyDetails().getOwners().forEach(owner -> {
			
			try {
				if (rs.getString("docid") != null && rs.getBoolean("docis_active") && docOwnerDetailId.equals(owner.getOwnerDetails().getId())) {

					AuditDetails docAuditdetails = AuditDetails.builder().createdBy(rs.getString("dcreated_by"))
							.createdTime(rs.getLong("dcreated_time")).lastModifiedBy(rs.getString("dmodified_by"))
							.lastModifiedTime(rs.getLong("dmodified_time")).build();

					OwnerDocument ownerDocument = OwnerDocument.builder().id(rs.getString("docid"))
							.ownerDetailsId(rs.getString("docowner_details_id")).tenantId(rs.getString("doctenantid"))
							.isActive(rs.getBoolean("docis_active")).documentType(rs.getString("document_type"))
							.fileStoreId(rs.getString("file_store_id"))
							.auditDetails(docAuditdetails).build();
					owner.getOwnerDetails().addOwnerDocumentsItem(ownerDocument);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

//		String courtCasePropertDetailId = rs.getString("ccproperty_details_id");
		if (courtCasePropertDetailId != null
				&& courtCasePropertDetailId.equals(property.getPropertyDetails().getId())) {

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("cccreated_by"))
					.createdTime(rs.getLong("cccreated_time")).lastModifiedBy(rs.getString("ccmodified_by"))
					.lastModifiedTime(rs.getLong("ccmodified_time")).build();

			CourtCase courtCase = CourtCase.builder().id(rs.getString("ccid"))
					.propertyDetailsId(courtCasePropertDetailId).tenantId(rs.getString("cctenantid"))
					.estateOfficerCourt(rs.getString("ccestate_officer_court"))
					.commissionersCourt(rs.getBoolean("cccommissioners_court"))
					.chiefAdministartorsCourt(rs.getString("ccchief_administartors_court"))
					.advisorToAdminCourt(rs.getString("ccadvisor_to_admin_court"))
					.honorableDistrictCourt(rs.getString("cchonorable_district_court"))
					.honorableHighCourt(rs.getString("cchonorable_high_court"))
					.honorableSupremeCourt(rs.getString("cchonorable_supreme_court")).auditDetails(auditdetails)
					.build();

			property.getPropertyDetails().addCourtCaseItem(courtCase);

		}

//		String purchaseDetailPropertyDetailId = rs.getString("pdproperty_details_id");
		if (purchaseDetailPropertyDetailId != null
				&& purchaseDetailPropertyDetailId.equals(property.getPropertyDetails().getId())) {

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pdcreated_by"))
					.createdTime(rs.getLong("pdcreated_time")).lastModifiedBy(rs.getString("pdmodified_by"))
					.lastModifiedTime(rs.getLong("pdmodified_time")).build();

			PurchaseDetails purchaseDetails = PurchaseDetails.builder().id(rs.getString("pdid"))
					.propertyDetailsId(purchaseDetailPropertyDetailId).tenantId(rs.getString("pdtenantid"))
					.newOwnerName(rs.getString("pdnew_owner_name"))
					.newOwnerFatherName(rs.getBoolean("pdnew_owner_father_name"))
					.newOwnerAddress(rs.getString("pdnew_owner_address"))
					.newOwnerMobileNumber(rs.getString("pdnew_owner_mobile_number"))
					.sellerName(rs.getString("pdseller_name")).sellerFatherName(rs.getString("pdseller_father_name"))
					.percentageOfShare(rs.getString("pdpercentage_of_share"))
					.modeOfTransfer(rs.getString("pdmode_of_transfer"))
					.registrationNumber(rs.getString("pdregistration_number"))
					.dateOfRegistration(rs.getLong("pddate_of_registration")).auditDetails(auditdetails).build();

			property.getPropertyDetails().addPurchaseDetailsItem(purchaseDetails);

		}

	}

}
