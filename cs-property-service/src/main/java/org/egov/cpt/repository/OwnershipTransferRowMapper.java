package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.egov.cpt.models.Property;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class OwnershipTransferRowMapper implements ResultSetExtractor<List<Property>> {

	@Override
	public List<Property> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Property> propertyMap = new LinkedHashMap<>();

		while (rs.next()) {
			String propertyId = rs.getString("pid");
			Property currentProperty = propertyMap.get(propertyId);
			String tenantId = rs.getString("pttenantid");
			Boolean permanent = rs.getBoolean("permanent");

			if (null == currentProperty && permanent == false && permanent != null) {
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

		Boolean permanent = rs.getBoolean("permanent");
		if (rs.getString("oid") != null && permanent == false && permanent != null) {

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

			Owner owners = Owner.builder().id(rs.getString("oid")).propertyId(rs.getString("oproperty_id"))
					.tenantId(rs.getString("otenantid")).allotmenNumber(rs.getString("oallotmen_number"))
					.applicationStatus(rs.getString("oapplication_status")).activeState(rs.getBoolean("oactive_state"))
					.isPrimaryOwner(rs.getString("ois_primary_owner")).ownerDetails(ownerDetails)
					.auditDetails(auditdetails).build();

			property.addOwnerItem(owners);
		}

	}

}
