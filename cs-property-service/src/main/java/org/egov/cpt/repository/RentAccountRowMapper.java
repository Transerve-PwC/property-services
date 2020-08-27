package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.RentAccount;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class RentAccountRowMapper implements ResultSetExtractor<RentAccount> {


	@Override
	public RentAccount extractData(ResultSet rs) throws SQLException, DataAccessException {
		RentAccount rentAccount=null;
		while (rs.next()) {
				String accountId=rs.getString("account_id");

					AuditDetails accountAuditDetails = AuditDetails.builder().createdBy(rs.getString("account_created_by"))
							.createdTime(rs.getLong("account_created_date")).lastModifiedBy(rs.getString("account_modified_by"))
							.lastModifiedTime(rs.getLong("account_modified_date")).build();
					 rentAccount = RentAccount.builder().id(rs.getString("account_id"))
							.propertyId("account_pid")
							.remainingAmount(rs.getDouble("account_remainingAmount"))
							.tenantId(rs.getString("account_tenantid"))
							.auditDetails(accountAuditDetails)
							.build();
		}
		return rentAccount;
	}

}
