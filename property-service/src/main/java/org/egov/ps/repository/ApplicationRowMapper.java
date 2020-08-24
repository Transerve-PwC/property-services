package org.egov.ps.repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.ps.model.Application;
import org.egov.ps.model.Document;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.web.contracts.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationRowMapper implements ResultSetExtractor<List<Application>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Application> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Application> applicationMap = new LinkedHashMap<>();

		while (rs.next()) {

			Application currentApplication = null;
			String applicationOwnerId = null;

			if (hasColumn(rs, "appid")) {
				String applicationId = rs.getString("appid");
				currentApplication = applicationMap.get(applicationId);

				if (null == currentApplication) {
					AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("appcreated_by"))
							.createdTime(rs.getLong("appcreated_time"))
							.lastModifiedBy(rs.getString("applast_modified_by"))
							.lastModifiedTime(rs.getLong("applast_modified_time")).build();

					currentApplication = Application.builder().id(applicationId).tenantId(rs.getString("apptenantid"))
							.applicationNumber(rs.getString("appapplication_number"))
							.branchType(rs.getString("appbranch_type")).moduleType(rs.getString("appmodule_type"))
							.applicationType(rs.getString("appapplication_type")).comments(rs.getString("appcomments"))
							.hardcopyReceivedDate(rs.getLong("apphardcopy_received_date"))
							.state(rs.getString("appstate")).action(rs.getString("appaction"))
							.auditDetails(auditdetails).build();

					PGobject applicationDetailsPgObject = (PGobject) rs.getObject("appapplication_details");
					if (applicationDetailsPgObject != null) {
						JsonNode applicationDetails = null;
						try {
							applicationDetails = mapper.readTree(applicationDetailsPgObject.getValue());

							if (applicationDetails.get("owner") != null) {
								JsonNode owner = applicationDetails.get("owner");
								applicationOwnerId = owner.get("id").asText();
							}

							currentApplication.setApplicationDetails(applicationDetails);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					applicationMap.put(applicationId, currentApplication);
				}
			}
			addChildrenToApplication(rs, currentApplication, applicationMap, applicationOwnerId);
		}

		return new ArrayList<>(applicationMap.values());

	}

	private void addChildrenToApplication(ResultSet rs, Application currentApplication,
			LinkedHashMap<String, Application> applicationMap, String applicationOwnerId) throws SQLException {

		if (hasColumn(rs, "docid")) {
			String docApplicationId = rs.getString("docapplication_id");

			try {
				if (rs.getString("docid") != null && rs.getBoolean("docis_active")
						&& docApplicationId.equals(currentApplication.getId())) {

					AuditDetails docAuditdetails = AuditDetails.builder().createdBy(rs.getString("dcreated_by"))
							.createdTime(rs.getLong("dcreated_time")).lastModifiedBy(rs.getString("dmodified_by"))
							.lastModifiedTime(rs.getLong("dmodified_time")).build();

					Document applicationDocuments = Document.builder().id(rs.getString("docid"))
							.referenceId(docApplicationId).tenantId(rs.getString("doctenantid"))
							.isActive(rs.getBoolean("docis_active")).documentType(rs.getString("document_type"))
							.fileStoreId(rs.getString("file_store_id")).propertyId(rs.getString("docproperty_id"))
							.auditDetails(docAuditdetails).build();
					currentApplication.addApplicationDocumentsItem(applicationDocuments);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (hasColumn(rs, "ptid")) {
			try {
				if (rs.getString("ptid") != null) {

					List<Owner> ownersList = addOwner(rs, applicationOwnerId);

					PropertyDetails propertyDetails = PropertyDetails.builder().id(rs.getString("ptdlid"))
							.propertyId(rs.getString("ptdlproperty_id")).propertyType(rs.getString("ptdlproperty_type"))
							.tenantId(rs.getString("pttenantid"))
							.typeOfAllocation(rs.getString("ptdltype_of_allocation"))
							.serviceCategory(rs.getString("ptdlservice_category")).owners(ownersList).build();

					Property property = Property.builder().id(rs.getString("ptid"))
							.fileNumber(rs.getString("ptfile_number")).tenantId(rs.getString("pttenantid"))
							.category(rs.getString("ptcategory")).subCategory(rs.getString("ptsub_category"))
							.sectorNumber(rs.getString("ptsector_number")).siteNumber(rs.getString("ptsite_number"))
							.propertyDetails(propertyDetails).build();

					currentApplication.setProperty(property);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private List<Owner> addOwner(ResultSet rs, String applicationOwnerId) throws SQLException {
		List<Owner> ownersList = new ArrayList<Owner>();
		if (hasColumn(rs, "oid")) {
			String ownerId = rs.getString("oid");
			String ownerDetailId = rs.getString("odid");
			String OwnerPropertyDetailId = rs.getString("oproperty_details_id");

			if (applicationOwnerId != null && ownerId != null && ownerId.contentEquals(applicationOwnerId)) {
				OwnerDetails ownerDetails = OwnerDetails.builder().id(ownerDetailId).ownerId(rs.getString("odowner_id"))
						.ownerName(rs.getString("odowner_name")).tenantId(rs.getString("odtenantid"))
						.guardianName(rs.getString("odguardian_name"))
						.guardianRelation(rs.getString("odguardian_relation"))
						.mobileNumber(rs.getString("odmobile_number"))
						.allotmentNumber(rs.getString("odallotment_number"))
						.dateOfAllotment(rs.getLong("oddate_of_allotment")).dueAmount(rs.getBigDecimal("oddue_amount"))
						.address(rs.getString("odaddress")).build();

				Owner owners = Owner.builder().id(ownerId).propertyDetailsId(OwnerPropertyDetailId)
						.tenantId(rs.getString("otenantid")).serialNumber(rs.getString("oserial_number"))
						.share(rs.getString("oshare")).cpNumber(rs.getString("ocp_number")).ownerDetails(ownerDetails)
						.build();

				ownersList.add(owners);
			}
		}
		return ownersList;
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
