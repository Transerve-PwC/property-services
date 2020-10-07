package org.egov.ps.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.ps.model.Application;
import org.egov.ps.model.ApplicationCriteria;
import org.egov.ps.model.Document;
import org.egov.ps.model.Owner;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class PropertyRepository {

	@Autowired
	private PropertyQueryBuilder propertyQueryBuilder;

	@Autowired
	private PropertyRowMapper propertyRowMapper;

	@Autowired
	private ApplicationQueryBuilder applicationQueryBuilder;

	@Autowired
	private ApplicationRowMapper applicationRowMapper;

	@Autowired
	private DocumentsRowMapper documentRowMapper;

	@Autowired
	private OwnerRowMapper ownerRowMapper;

	@Autowired
	WorkflowIntegrator workflowIntegrator;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Property> getProperties(PropertyCriteria criteria) {

		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = propertyQueryBuilder.getPropertySearchQuery(criteria, preparedStmtList);
		List<Property> properties = namedParameterJdbcTemplate.query(query, preparedStmtList, propertyRowMapper);
		if (CollectionUtils.isEmpty(properties)) {
			return properties;
		}
		List<String> relations = criteria.getRelations();
		if (CollectionUtils.isEmpty(relations)) {
			if (properties.size() == 1) {
				relations = new ArrayList<String>();
				relations.add(PropertyQueryBuilder.RELATION_OWNER);
				relations.add(PropertyQueryBuilder.RELATION_OWNER_DOCUMENTS);
				relations.add(PropertyQueryBuilder.RELATION_COURT);
			}
		}
		if (relations.contains(PropertyQueryBuilder.RELATION_OWNER)) {
			this.addOwnersToProperties(properties);
		}
		if (relations.contains(PropertyQueryBuilder.RELATION_OWNER_DOCUMENTS)) {
			this.addOwnerDocumentsToProperties(properties);
		}
		return properties;
	}

	private void addOwnersToProperties(List<Property> properties) {
		if (CollectionUtils.isEmpty(properties)) {
			return;
		}
		/**
		 * Extract property detail ids.
		 */
		List<String> propertyDetailsIds = properties.stream().map(property -> property.getPropertyDetails().getId())
				.collect(Collectors.toList());

		/**
		 * Fetch owners from database
		 */
		Map<String, Object> params = new HashMap<String, Object>(1);
		String ownerDocsQuery = propertyQueryBuilder.getOwnersQuery(propertyDetailsIds, params);
		List<Owner> owners = namedParameterJdbcTemplate.query(ownerDocsQuery, params, ownerRowMapper);

		/**
		 * Assign owners to corresponding properties
		 */
		properties.stream().forEach(property -> {
			property.getPropertyDetails().setOwners(owners.stream().filter(
					owner -> owner.getPropertyDetailsId().equalsIgnoreCase(property.getPropertyDetails().getId()))
					.collect(Collectors.toList()));
		});
	}

	private void addOwnerDocumentsToProperties(List<Property> properties) {
		if (CollectionUtils.isEmpty(properties)) {
			return;
		}
		/**
		 * Extract ownerIds
		 */
		List<Owner> owners = properties.stream().map(property -> property.getPropertyDetails().getOwners())
				.flatMap(Collection::stream).collect(Collectors.toList());
		List<String> ownerDetailIds = owners.stream().map(owner -> owner.getOwnerDetails().getId())
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(ownerDetailIds)) {
			return;
		}
		/**
		 * Fetch documents from database.
		 */
		Map<String, Object> params = new HashMap<String, Object>(1);
		String ownerDocsQuery = propertyQueryBuilder.getOwnerDocsQuery(ownerDetailIds, params);
		List<Document> documents = namedParameterJdbcTemplate.query(ownerDocsQuery, params, documentRowMapper);

		/**
		 * Assign documents to corresponding owners.
		 */
		owners.stream().forEach(owner -> {
			owner.getOwnerDetails()
					.setOwnerDocuments(documents.stream().filter(
							document -> document.getReferenceId().equalsIgnoreCase(owner.getOwnerDetails().getId()))
							.collect(Collectors.toList()));
		});
	}

	public Property findPropertyById(String propertyId) {
		PropertyCriteria propertySearchCriteria = PropertyCriteria.builder().propertyId(propertyId).build();
		List<Property> properties = this.getProperties(propertySearchCriteria);
		if (properties == null || properties.isEmpty()) {
			return null;
		}
		return properties.get(0);
	}

	public List<Application> getApplications(ApplicationCriteria criteria) {
		Map<String, Object> preparedStmtList = new HashMap<>();
		String query = applicationQueryBuilder.getApplicationSearchQuery(criteria, preparedStmtList);
		return namedParameterJdbcTemplate.query(query, preparedStmtList, applicationRowMapper);
	}
}
