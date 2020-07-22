package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.DuplicateCopyDocument;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.MortgageApplicant;
import org.egov.cpt.models.MortgageApprovedGrantDetails;
import org.egov.cpt.models.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MortgageRowMapper implements ResultSetExtractor<List<Mortgage>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Mortgage> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Mortgage> applicationMap = new HashMap<>();
		while (rs.next()) {
			String mortgageId = rs.getString("mgid");
			Mortgage currentapplication = applicationMap.get(mortgageId);

			if (null == currentapplication) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
						.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("modified_by"))
						.lastModifiedTime(rs.getLong("modified_time")).build();

				// List<Owner> owners = addOwnersToProperty(rs, currentProperty);

				Property property = Property.builder().id(rs.getString("pid"))
						.transitNumber(rs.getString("transit_number")).colony(rs.getString("colony"))
						.pincode(rs.getString("pincode")).build();

				currentapplication = Mortgage.builder().id(mortgageId).property(property)
						.tenantId(rs.getString("tenantid")).state(rs.getString("state")).action(rs.getString("action"))
						.applicationNumber(rs.getString("app_number")).auditDetails(auditdetails).build();
				applicationMap.put(mortgageId, currentapplication);
			}
			addChildrenToProperty(rs, currentapplication);
		}
		return new ArrayList<>(applicationMap.values());

	}

	private void addChildrenToProperty(ResultSet rs, Mortgage currentapplication) throws SQLException {
		Map<String, MortgageApplicant> applicantMap = new HashMap<>();
		MortgageApplicant applicant = null;

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_time")).lastModifiedBy(rs.getString("modified_by"))
				.lastModifiedTime(rs.getLong("created_time")).build();

		if (currentapplication.getApplicant() == null) {
			if (rs.getString("aid") != null) {
				applicant = MortgageApplicant.builder().id(rs.getString("aid")).tenantId(rs.getString("aptenantid"))
						.mortgageId(rs.getString("mg_id")).name(rs.getString("name")).email(rs.getString("email"))
						.phone(rs.getString("mobileno")).guardian(rs.getString("guardian"))
						.relationship(rs.getString("relationship")).adhaarNumber(rs.getString("adhaarnumber"))
						.auditDetails(auditDetails).build();
				applicantMap.put(rs.getString("aid"), applicant);
				currentapplication.setApplicant(new ArrayList<>(applicantMap.values()));
			}
		}

		if (currentapplication.getProperty() == null) {
			Property property = Property.builder().id(rs.getString("pid")).transitNumber(rs.getString("transit_number"))
					.build();
			currentapplication.setProperty(property);
		}

		if (currentapplication.getMortgageApprovedGrantDetails() == null) {
			AuditDetails magdAuditDetails = AuditDetails.builder().createdBy(rs.getString("gdcreated_by"))
					.createdTime(rs.getLong("gdcreated_time")).lastModifiedBy(rs.getString("gdmodified_by"))
					.lastModifiedTime(rs.getLong("gdmodified_time")).build();
			MortgageApprovedGrantDetails mortgageApprovedGrantDetails = MortgageApprovedGrantDetails.builder()
					.id(rs.getString("gdid")).propertyDetailId(rs.getString("gdproperty_detail_id"))
					.ownerId(rs.getString("gdowner_id")).tenentId(rs.getString("gdtenantid"))
					.bankName(rs.getString("gdbank_name")).mortgageAmount(rs.getBigDecimal("gdmortgage_amount"))
					.sanctionLetterNumber(rs.getString("gdsanction_letter_number"))
					.sanctionDate(rs.getLong("gdsanction_date")).mortgageEndDate(rs.getLong("gdmortgage_end_date"))
					.auditDetails(magdAuditDetails).build();
			currentapplication.addMortgageApprovedGrantDetails(mortgageApprovedGrantDetails);
		}

		if (rs.getString("docId") != null && rs.getBoolean("doc_active")) {
			DuplicateCopyDocument applicationDocument = DuplicateCopyDocument.builder()
					.documentType(rs.getString("doctype")).fileStoreId(rs.getString("doc_filestoreid"))
					.id(rs.getString("docId")).tenantId(rs.getString("doctenantid")).active(rs.getBoolean("doc_active"))
					.applicationId(rs.getString("doc_mgid")).auditDetails(auditDetails).build();
			currentapplication.addApplicationDocumentsItem(applicationDocument);
		}

	}

}