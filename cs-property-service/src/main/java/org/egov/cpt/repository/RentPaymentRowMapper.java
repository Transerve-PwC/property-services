package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.models.RentPayment.ModeEnum;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RentPaymentRowMapper implements RowMapper<RentPayment>{
	
	@Override
	public RentPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_date")).lastModifiedBy(rs.getString("modified_by"))
				.lastModifiedTime(rs.getLong("modified_date")).build();
		
		return RentPayment.builder()
				.auditDetails(auditdetails)
				.id(rs.getString("id"))
				.tenantId(rs.getString("tenantid"))
				.amountPaid(rs.getDouble("amountpaid"))
				.receiptNo(rs.getString("receiptno"))
				.dateOfPayment(rs.getLong("dateofpayment"))
				.mode(ModeEnum.fromValue(rs.getString("mode")))
				.propertyId(rs.getString("property_id"))
				.build();
	}

}
