package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.Address;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Document;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PropertyRowMapper implements ResultSetExtractor<List<Property>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, Property> propertyMap = new HashMap<>();
		while (rs.next()) {
			String propertyId = rs.getString("pid");
			Property currentProperty = propertyMap.get(propertyId);

			if (null == currentProperty) {
				AuditDetails auditdetails = getAuditDetail(rs, "property");
				PropertyDetails propertyDetails = getPropertyDetails(rs, "property");
				List<Owner> owners = addOwnersToProperty(rs, currentProperty);

				currentProperty = Property.builder().id(propertyId).transitNumber(rs.getString("transit_number"))
						.tenantId(rs.getString("pttenantid")).colony(rs.getString("colony"))
						.masterDataState(rs.getString("master_data_state"))
						.masterDataAction(rs.getString("master_data_action")).auditDetails(auditdetails)
						.propertyDetails(propertyDetails).owners(owners).build();
				propertyMap.put(propertyId, currentProperty);
			}
		}
		return new ArrayList<>(propertyMap.values());
	}

	/**
	 * prepares and returns an audit detail object
	 * 
	 * depending on the source the column names of result set will vary
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AuditDetails getAuditDetail(ResultSet rs, String source) throws SQLException {
		switch (source) {
		case "property":
			return AuditDetails.builder().createdBy(rs.getString("created_by")).createdTime(rs.getLong("created_date"))
					.lastModifiedBy(rs.getString("modified_by")).lastModifiedTime(rs.getLong("modified_date")).build();
		default:
			return null;
		}
	}

	private PropertyDetails getPropertyDetails(ResultSet rs, String source) throws SQLException {
		Address address = getAddress(rs);
		String propertyId = rs.getString("pdid");
		AuditDetails auditdetails = getAuditDetail(rs, "property");
		List<Document> applicationDocuments = getApplicationDocuments(rs);
		switch (source) {
		case "property":
//			if (pgObj != null) {
//
//				try {
//					additionalDetail = mapper.readTree(pgObj.getValue());
//				} catch (IOException e) {
//					throw new CustomException("PARSING ERROR", "The additional_details json cannot be parsed");
//				}
//			}
		}
		return PropertyDetails.builder().id(propertyId).propertyId(rs.getString("pdproperty_id"))
				.transitNumber(rs.getString("transit_number")).tenantId(rs.getString("pdtenantid"))
				.area(rs.getString("area")).rentPerSqyd(rs.getString("rent_per_sqyd"))
				.currentOwner(rs.getString("current_owner")).floors(rs.getString("floors"))
				.additionalDetails(rs.getString("additional_details")).address(address).auditDetails(auditdetails)
				.applicationDocuments(applicationDocuments).build();

	}

	private List<Document> getApplicationDocuments(ResultSet rs) throws SQLException {

		Map<String, Document> documentMap = new HashMap<>();
		AuditDetails auditdetails = getAuditDetail(rs, "property");
		String docId = rs.getString("docid");
		Document currentDocument = documentMap.get(docId);

		if (null == currentDocument) {
			currentDocument = Document.builder().id(rs.getString("docid")).propertyId(rs.getString("docproperty_id"))
					.tenantId(rs.getString("doctenantid")).active(rs.getBoolean("docis_active"))
					.documentType(rs.getString("document_type")).fileStoreId(rs.getString("fileStore_id"))
					.documentUid(rs.getString("document_uid")).auditDetails(auditdetails).build();
			documentMap.put(docId, currentDocument);
		}
		return new ArrayList<>(documentMap.values());
	}

	/**
	 * creates and adds the address object to property
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Address getAddress(ResultSet rs) throws SQLException {

//		Boundary locality = Boundary.builder().code(rs.getString("locality")).build();

		/*
		 * id of the address table is being fetched as address key to avoid confusion
		 * with addressId field
		 */
//		Double latitude = rs.getDouble("latitude");
//		if (rs.wasNull()) {
//			latitude = null;
//		}
//		
//		Double longitude = rs.getDouble("longitude");
//		if (rs.wasNull()) {
//			longitude = null;
//		}

		AuditDetails auditdetails = getAuditDetail(rs, "property");
		Address address = Address.builder().id(rs.getString("aid")).propertyId(rs.getString("aproperty_id"))
				.transitNumber(rs.getString("atransit_number")).tenantId(rs.getString("atenantid"))
				.colony(rs.getString("colony")).area(rs.getString("addressArea")).district(rs.getString("district"))
				.state(rs.getString("state")).country(rs.getString("country")).pincode(rs.getString("pincode"))
				.landmark(rs.getString("landmark")).auditDetails(auditdetails).build();
		return address;
	}

	/**
	 * Adds Owner Object to Property
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Owner> addOwnersToProperty(ResultSet rs, Property property) throws SQLException {

		Map<String, Owner> ownerMap = new HashMap<>();
		AuditDetails auditdetails = getAuditDetail(rs, "property");
		String ownerId = rs.getString("owner_id");
		Owner currentOwner = ownerMap.get(ownerId);

		if (null == currentOwner) {
			OwnerDetails ownerDetails = getOwnerDetails(rs, "property");
			currentOwner = Owner.builder().id(rs.getString("oid")).propertyId(rs.getString("oproperty_id"))
					.tenantId(rs.getString("otenantid")).allotmenNumber(rs.getString("allotmen_number"))
					.applicationStatus(rs.getString("application_status")).activeState(rs.getBoolean("active_state"))
					.isPrimaryOwner(rs.getString("is_primary_owner")).ownerDetails(ownerDetails)
					.auditDetails(auditdetails).build();

			ownerMap.put(ownerId, currentOwner);
		}
		return new ArrayList<>(ownerMap.values());
	}

	private OwnerDetails getOwnerDetails(ResultSet rs, String source) throws SQLException {
		switch (source) {
		case "property":
			Address correspondenceAddress = getCorrespondenceAddress(rs);
			AuditDetails auditdetails = getAuditDetail(rs, "property");
			return OwnerDetails.builder().id(rs.getString("odid")).propertyId(rs.getString("oproperty_id"))
					.ownerId(rs.getString("owner_id")).tenantId(rs.getString("otenantid")).name(rs.getString("name"))
					.email(rs.getString("email")).phone(rs.getString("phone")).gender(rs.getString("gender"))
					.dateOfBirth(rs.getLong("date_of_birth")).aadhaarNumber(rs.getString("aadhaar_number"))
					.allotmentStartdate(rs.getLong("allotment_startdate"))
					.allotmentEnddate(rs.getLong("allotment_enddate"))
					.posessionStartdate(rs.getLong("posession_startdate"))
					.posessionEnddate(rs.getLong("posession_enddate")).monthlyRent(rs.getString("monthly_rent"))
					.revisionPeriod(rs.getString("revision_period"))
					.revisionPercentage(rs.getString("revision_percentage"))
					.fatherOrHusband(rs.getString("father_or_husband")).relation(rs.getString("relation"))
					.correspondenceAddress(correspondenceAddress).auditDetails(auditdetails).payment(null).build();
		default:
			return null;
		}
	}

	private Address getCorrespondenceAddress(ResultSet rs) throws SQLException {
		AuditDetails auditdetails = getAuditDetail(rs, "property");
		Address address = Address.builder().id(rs.getString("aid")).propertyId(rs.getString("aproperty_id"))
				.transitNumber(rs.getString("atransit_number")).tenantId(rs.getString("atenantid"))
				.colony(rs.getString("colony")).area(rs.getString("addressArea")).district(rs.getString("district"))
				.state(rs.getString("state")).country(rs.getString("country")).pincode(rs.getString("pincode"))
				.landmark(rs.getString("landmark")).auditDetails(auditdetails).build();
		return address;
	}

	/**
	 * method parses the PGobject and returns the JSON node
	 * 
	 * @param rs
	 * @param key
	 * @return
	 * @throws SQLException
	 */
//	private Object getadditionalDetail(ResultSet rs, String key) throws SQLException {
//
//		JsonNode propertyAdditionalDetails = null;
//
//		try {
//
//			PGobject obj = (PGobject) rs.getObject(key);
//			if (obj != null) {
//				propertyAdditionalDetails = mapper.readTree(obj.getValue());
//			}
//
//		} catch (IOException e) {
//			throw new CustomException("PARSING ERROR", "The propertyAdditionalDetail json cannot be parsed");
//		}
//
//		return propertyAdditionalDetails;
//
//	}
}
