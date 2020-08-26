package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentDemand.ModeEnum;
import org.springframework.jdbc.core.RowMapper;

public class RentDemandRowMapper implements RowMapper<RentDemand>{

	@Override
	public RentDemand mapRow(ResultSet rs, int rowNum) throws SQLException {
		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_date")).lastModifiedBy(rs.getString("modified_by"))
				.lastModifiedTime(rs.getLong("modified_date")).build();
		
		return RentDemand.builder()
				.auditDetails(auditdetails)
				.id(rs.getString("id"))
				.propertyId(rs.getString("property_id"))
				.initialGracePeriod(rs.getInt("initialgraceperiod"))
				.generationDate(rs.getLong("generationdate"))
				.collectionPrincipal(rs.getDouble("collectionprincipal"))
				.remainingPrincipal(rs.getDouble("remainingprincipal"))
				.interestSince(rs.getLong("interestsince"))
				.mode(ModeEnum.fromValue(rs.getString("mode")))
				.tenantId(rs.getString("tenantid"))
				.build();
	}
}
