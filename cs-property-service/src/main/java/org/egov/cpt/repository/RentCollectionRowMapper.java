package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentCollection.CollectionAgainst;
import org.springframework.jdbc.core.RowMapper;

public class RentCollectionRowMapper implements RowMapper<RentCollection>{

	@Override
	public RentCollection mapRow(ResultSet rs, int rowNum) throws SQLException {
		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_date")).lastModifiedBy(rs.getString("modified_by"))
				.lastModifiedTime(rs.getLong("modified_date")).build();
		
		return RentCollection.builder()
				.auditDetails(auditdetails)
				.id(rs.getString("id"))
				.tenantId(rs.getString("tenantid"))
				.paymentId(rs.getString("payment_id"))
				.demandId(rs.getString("demand_id"))
				.collectionAgainst(CollectionAgainst.fromValue(rs.getString("collectionagainst")))
				.interestCollected(rs.getDouble("interestcollected"))
				.principalCollected(rs.getDouble("principalcollected"))
				.build();
	}
}
