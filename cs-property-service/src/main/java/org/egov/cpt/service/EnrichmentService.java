package org.egov.cpt.service;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {

	@Autowired
	PropertyUtil propertyutil;

	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		Property property = request.getProperties().get(0); // TODO change to list later
//
		property.setId(UUID.randomUUID().toString());
//		property.setAccountId(requestInfo.getUserInfo().getUuid());
//		if (!CollectionUtils.isEmpty(property.getDocuments()))
//			property.getDocuments().forEach(doc -> {
//				doc.setId(UUID.randomUUID().toString());
//				doc.setStatus(Status.ACTIVE);
//			});
//
//		property.getAddress().setTenantId(property.getTenantId());
//		property.getAddress().setId(UUID.randomUUID().toString());
//
//		property.getOwners().forEach(owner -> {
//
//			if (!CollectionUtils.isEmpty(owner.getDocuments()))
//				owner.getDocuments().forEach(doc -> {
//					doc.setId(UUID.randomUUID().toString());
//					doc.setStatus(Status.ACTIVE);
//				});
//
//			owner.setStatus(Status.ACTIVE);
//		});
//
//		if (!CollectionUtils.isEmpty(property.getInstitution()))
//			property.getInstitution().forEach(institute -> institute.setId(UUID.randomUUID().toString()));
//
		property.setAuditDetails(propertyAuditDetails);
//
//		setIdgenIds(request);
//		enrichBoundary(request);
	}
}
