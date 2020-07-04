package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class OwnershipTransferRowMapper implements ResultSetExtractor<List<Owner>> {

	@Override
	public List<Owner> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, Owner> ownerMap = new HashMap<>();
		while (rs.next()) {
			String ownerId = rs.getString("oid");
			Boolean permanent = rs.getBoolean("permanent");
			Owner currentOwner = ownerMap.get(ownerId);

			if (null == currentOwner && permanent == false) {

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

				currentOwner = Owner.builder().id(rs.getString("oid")).propertyId(rs.getString("oproperty_id"))
						.tenantId(rs.getString("otenantid")).allotmenNumber(rs.getString("oallotmen_number"))
						.applicationStatus(rs.getString("oapplication_status"))
						.activeState(rs.getBoolean("oactive_state")).isPrimaryOwner(rs.getString("ois_primary_owner"))
						.ownerDetails(ownerDetails).auditDetails(auditdetails).build();

				ownerMap.put(ownerId, currentOwner);
			}
		}
		return new ArrayList<>(ownerMap.values());
	}

}
