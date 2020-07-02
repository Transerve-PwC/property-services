package org.egov.cpt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.cpt.models.Applicant;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Document;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.PropertyDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DuplicateCopyPropertyRowMapper implements ResultSetExtractor<List<DuplicateCopy>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<DuplicateCopy> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, DuplicateCopy> propertyMap = new HashMap<>();
		while (rs.next()) {
			String propertyId = rs.getString("pid");
			DuplicateCopy currentProperty = propertyMap.get(propertyId);

			if (null == currentProperty) {
				AuditDetails auditdetails = AuditDetails.builder().
						createdBy(rs.getString("created_by"))
						.createdTime(rs.getLong("created_time"))
						.lastModifiedBy(rs.getString("modified_by"))
						.lastModifiedTime(rs.getLong("modified_time")).build();

				//				List<Owner> owners = addOwnersToProperty(rs, currentProperty);

				currentProperty = DuplicateCopy.builder()
						.id(propertyId)
						.transitNumber(rs.getString("transit_number"))
						.tenantId(rs.getString("tenantid"))
						.state(rs.getString("state"))
						.action(rs.getString("action"))
						.propertyDetails(null)
						.auditDetails(auditdetails).build();
				propertyMap.put(propertyId, currentProperty);
			}
			addChildrenToProperty(rs, currentProperty);
		}
		return new ArrayList<>(propertyMap.values());
	}

	private void addChildrenToProperty(ResultSet rs, DuplicateCopy currentProperty) throws SQLException {
		Map<String, Applicant> applicantMap = new HashMap<>();
		Map<String, Document> documentMap = new HashMap<>();
		Applicant applicant = null;
		if(currentProperty.getPropertyDetails()==null){
			if(rs.getString("ptdl_id")!=null){
				PropertyDetails propertyDetails = PropertyDetails.builder()
						.id(rs.getString("ptdl_id"))
						.transitNumber(rs.getString("ptdl_tra_number"))
						.build();
				currentProperty.setPropertyDetails(propertyDetails);
			}
		}

		AuditDetails auditDetails = AuditDetails.builder()
				.createdBy(rs.getString("created_by"))
				.createdTime(rs.getLong("created_date"))
				.lastModifiedBy(rs.getString("modified_by"))
				.lastModifiedTime(rs.getLong("modified_date")).build();

		if(currentProperty.getApplicant()==null){
			if(rs.getString("aid")!=null ){ 
				applicant = Applicant.builder()
						.id(rs.getString("aid"))
						.tenantId(rs.getString("aptenantid"))
						.propertyId(rs.getString("approperty_id"))
						.name(rs.getString("name"))
						.email(rs.getString("email"))
						.phone(rs.getString("mobileno"))
						.guardian(rs.getString("guardian"))
						.relationship(rs.getString("relationship"))
						.adhaarNumber(rs.getString("adhaarnumber"))
						.auditDetails(auditDetails)
						.build();
				applicantMap.put(rs.getString("aid"), applicant);
				currentProperty.setApplicant(new ArrayList<>(applicantMap.values()));
			}
		}


		if(rs.getString("docId")!=null && rs.getBoolean("doc_active")) {
			Document applicationDocument = Document.builder()
					.documentType(rs.getString("doctype"))
					.fileStoreId(rs.getString("doc_filestoreid"))
					.id(rs.getString("docId"))
					.tenantId(rs.getString("doctenantid"))
					.active(rs.getBoolean("doc_active"))
					.auditDetails(auditDetails)
					.build();
			currentProperty.getPropertyDetails().addApplicationDocumentsItem(applicationDocument);
		}

		/*if(rs.getString("adid")!=null) {
			Address address = Address.builder()
					.id(rs.getString("adid"))
					.propertyId(rs.getString("aproperty_id"))
					.transitNumber(rs.getString("atransit_number"))
					.tenantId(rs.getString("atenantid"))
					.area(rs.getString("addressArea"))
					.pincode(rs.getString("pincode"))
					.auditDetails(auditDetails).build();
			currentProperty.getPropertyDetails().setAddress(address);
		}*/

	}

	}
