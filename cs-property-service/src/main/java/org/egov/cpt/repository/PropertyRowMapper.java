package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
						.masterDataAction(rs.getString("master_data_action")).auditDetails(auditdetails).build();
				propertyMap.put(propertyId, currentProperty);
			}
			addChildrenToProperty(rs, currentProperty);
		}
		return new ArrayList<>(propertyMap.values());
	}

	private void addChildrenToProperty(ResultSet rs, Property property) throws SQLException {

		String tenantId = property.getTenantId();
		String propertyDetailId = rs.getString("pdid");

		if (property.getPropertyDetails() == null) {

			Address address = Address.builder().id(rs.getString("aid")).propertyId(rs.getString("aproperty_id"))
					.transitNumber(rs.getString("atransit_number")).tenantId(tenantId).colony(rs.getString("colony"))
					.area(rs.getString("addressArea")).district(rs.getString("district")).state(rs.getString("state"))
					.country(rs.getString("country")).pincode(rs.getString("pincode"))
					.landmark(rs.getString("landmark")).build();

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("pcreated_by"))
					.createdTime(rs.getLong("pcreated_date")).lastModifiedBy(rs.getString("pmodified_by"))
					.lastModifiedTime(rs.getLong("pmodified_date")).build();

			PropertyDetails propertyDetails = PropertyDetails.builder().id(propertyDetailId)
					.propertyId(rs.getString("pdproperty_id")).transitNumber(rs.getString("transit_number"))
					.tenantId(tenantId).area(rs.getString("area")).rentPerSqyd(rs.getString("rent_per_sqyd"))
					.currentOwner(rs.getString("current_owner")).floors(rs.getString("floors"))
					.additionalDetails(rs.getString("additional_details")).address(address).auditDetails(auditdetails)
					.build();

			property.setPropertyDetails(propertyDetails);
		}

		String docPropertyId = rs.getString("docproperty_id");
		if (rs.getString("docid") != null && rs.getBoolean("docis_active") && docPropertyId.equals(property.getId())) {
			Document applicationDocument = Document.builder().id(rs.getString("docid"))
					.propertyId(rs.getString("docproperty_id")).tenantId(rs.getString("doctenantid"))
					.active(rs.getBoolean("docis_active")).documentType(rs.getString("document_type"))
					.fileStoreId(rs.getString("fileStore_id")).documentUid(rs.getString("document_uid")).build();
			property.getPropertyDetails().addApplicationDocumentsItem(applicationDocument);
		}

		String OwnerPropertyId = rs.getString("oproperty_id");
		if (rs.getString("oid") != null && OwnerPropertyId.equals(property.getId())) {

			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("ocreated_by"))
					.createdTime(rs.getLong("ocreated_date")).lastModifiedBy(rs.getString("omodified_by"))
					.lastModifiedTime(rs.getLong("omodified_date")).build();

			OwnerDetails ownerDetails = OwnerDetails.builder().id(rs.getString("odid"))
					.propertyId(rs.getString("oproperty_id")).ownerId(rs.getString("owner_id"))
					.tenantId(rs.getString("otenantid")).name(rs.getString("name")).email(rs.getString("email"))
					.phone(rs.getString("phone")).gender(rs.getString("gender"))
					.dateOfBirth(rs.getLong("date_of_birth")).aadhaarNumber(rs.getString("aadhaar_number"))
					.allotmentStartdate(rs.getLong("allotment_startdate"))
					.allotmentEnddate(rs.getLong("allotment_enddate"))
					.posessionStartdate(rs.getLong("posession_startdate"))
					.posessionEnddate(rs.getLong("posession_enddate")).monthlyRent(rs.getString("monthly_rent"))
					.revisionPeriod(rs.getString("revision_period"))
					.revisionPercentage(rs.getString("revision_percentage"))
					.fatherOrHusband(rs.getString("father_or_husband")).relation(rs.getString("relation"))
					.applicationType(OwnerDetails.ApplicationTypeEnum.fromValue(rs.getString("application_type")))
					.applicationNumber(rs.getString("application_number"))
					.dateOfDeathAllottee(rs.getLong("date_of_death_allottee"))
					.relationWithDeceasedAllottee(rs.getString("relation_with_deceased_allottee"))
					.auditDetails(auditdetails).payment(null).build();

			Owner owners = Owner.builder().id(rs.getString("oid")).property(property)
					.tenantId(rs.getString("otenantid")).allotmenNumber(rs.getString("oallotmen_number"))
					.activeState(rs.getBoolean("oactive_state")).isPrimaryOwner(rs.getString("ois_primary_owner"))
					.ownerDetails(ownerDetails).auditDetails(auditdetails).build();

			property.addOwnerItem(owners);
		}

	}

}
