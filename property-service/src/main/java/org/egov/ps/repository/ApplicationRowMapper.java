package org.egov.ps.repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.ps.model.Application;
import org.egov.ps.model.Property;
import org.egov.ps.web.contracts.AuditDetails;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationRowMapper implements ResultSetExtractor<List<Application>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Application> extractData(ResultSet rs) throws SQLException, DataAccessException {

		LinkedHashMap<String, Application> applicationMap = new LinkedHashMap<>();

		while (rs.next()) {

			Application currentApplication = null;

			if (hasColumn(rs, "appid")) {
				String applicationId = rs.getString("appid");
				currentApplication = applicationMap.get(applicationId);

				if (null == currentApplication) {
					AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("appcreated_by"))
							.createdTime(rs.getLong("appcreated_time"))
							.lastModifiedBy(rs.getString("applast_modified_by"))
							.lastModifiedTime(rs.getLong("applast_modified_time")).build();

					Property property = Property.builder().id(rs.getString("appproperty_id"))
							.fileNumber(rs.getString("ptfile_number")).build();

					currentApplication = Application.builder().id(applicationId).tenantId(rs.getString("apptenantid"))
							.property(property).applicationNumber(rs.getString("appapplication_number"))
							.branchType(rs.getString("appbranch_type")).moduleType(rs.getString("appmodule_type"))
							.applicationType(rs.getString("appapplication_type")).comments(rs.getString("appcomments"))
							.hardcopyReceivedDate(rs.getLong("apphardcopy_received_date"))
							.state(rs.getString("appstate")).action(rs.getString("appaction"))
							.auditDetails(auditdetails).build();

					PGobject applicationDetailsPgObject = (PGobject) rs.getObject("appapplication_details");
					if (applicationDetailsPgObject != null) {
						JsonNode applicationDetails;
						try {
							applicationDetails = mapper.readTree(applicationDetailsPgObject.getValue());
							currentApplication.setApplicationDetails(applicationDetails);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					applicationMap.put(applicationId, currentApplication);
				}
			}
		}

		return new ArrayList<>(applicationMap.values());

	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}

}
