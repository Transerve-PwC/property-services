package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Owner;
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

//				Address address = getAddress(rs, tenanId);
				AuditDetails auditdetails = getAuditDetail(rs, "property");
				PropertyDetails propertyDetails = getPropertyDetails(rs, "property");
				List<Owner> owners = addOwnersToProperty(rs, currentProperty);

				currentProperty = Property.builder().id(propertyId).transitNumber(rs.getString("transit_number"))
						.colony(rs.getString("colony")).masterDataState(rs.getString("master_data_state"))
						.masterDataAction(rs.getString("master_data_action")).auditDetails(auditdetails)
						.propertyDetails(propertyDetails).owners(owners).build();

				propertyMap.put(propertyId, currentProperty);
			}

		}

		return new ArrayList<>(propertyMap.values());

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

			currentOwner = Owner.builder().id(rs.getString("oid")).propertyId(rs.getString("oproperty_id"))
					.ownerId(rs.getString("owner_id")).name(rs.getString("name")).email(rs.getString("email"))
					.phone(rs.getString("phone")).gender(rs.getString("gender"))
					.dateOfBirth(rs.getString("date_of_birth")).aadhaarNumber(rs.getString("aadhaar_number"))
					.allotmentStartdate(rs.getString("allotment_startdate"))
					.allotmentEnddate(rs.getString("allotment_enddate"))
					.posessionStartdate(rs.getString("posession_startdate"))
					.posessionEnddate(rs.getString("posession_enddate")).allotmenNumber(rs.getString("allotmen_number"))
					.applicationStatus(rs.getString("application_status")).activeState(rs.getBoolean("active_state"))
					.isPrimaryOwner(rs.getString("is_primary_owner")).monthlyRent(rs.getString("monthly_rent"))
					.revisionPeriod(rs.getString("revision_period"))
					.revisionPercentage(rs.getString("revision_percentage")).auditDetails(auditdetails).payment(null)
					.build();

			ownerMap.put(ownerId, currentOwner);
		}

		return new ArrayList<>(ownerMap.values());
	}

	/**
	 * creates and adds the address object to property
	 * 
	 * @param rs
	 * @param tenanId
	 * @return
	 * @throws SQLException
	 */
//	private Address getAddress(ResultSet rs, String tenanId) throws SQLException {
//		
//		Boundary locality = Boundary.builder().code(rs.getString("locality")).build();
//
//		/*
//		 * id of the address table is being fetched as address key to avoid confusion
//		 * with addressId field
//		 */
//		Double latitude = rs.getDouble("latitude");
//		if (rs.wasNull()) {
//			latitude = null;
//		}
//		
//		Double longitude = rs.getDouble("longitude");
//		if (rs.wasNull()) {
//			longitude = null;
//		}
//
//		Address address = Address.builder()
//				.type(Type.fromValue(rs.getString("addresstype")))
//				.addressNumber(rs.getString("addressNumber"))
//				.addressLine1(rs.getString("addressLine1"))
//				.addressLine2(rs.getString("addressLine2"))
//				.buildingName(rs.getString("buildingName"))
//				.detail(rs.getString("addressdetail"))
//				.landmark(rs.getString("landmark"))
//				.pincode(rs.getString("pincode"))
//				.doorNo(rs.getString("doorno"))
//				.street(rs.getString("street"))
//				.id(rs.getString("addressId"))
//				.city(rs.getString("city"))
//				.latitude(latitude)
//				.locality(locality)
//				.longitude(longitude)
//				.tenantId(tenanId)
//				.build();
//		return address;
//	}
//	
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
		String propertyId = rs.getString("pdid");
		AuditDetails auditdetails = getAuditDetail(rs, "property");
//		PGobject pgObj = (PGobject) rs.getObject("additional_details");
//		JsonNode additionalDetail = null;
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
				.transitNumber(rs.getString("transit_number")).area(rs.getString("area"))
				.rentPerSqyd(rs.getString("rent_per_sqyd")).currentOwner(rs.getString("current_owner"))
				.floors(rs.getString("floors")).additionalDetails(rs.getString("additional_details"))
				.auditDetails(auditdetails).build();

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
