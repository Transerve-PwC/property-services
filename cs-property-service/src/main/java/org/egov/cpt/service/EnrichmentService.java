package org.egov.cpt.service;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EnrichmentService {

	@Autowired
	PropertyUtil propertyutil;

	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				String gen_property_id = UUID.randomUUID().toString();
				property.setId(gen_property_id);
				property.setProperty_id(gen_property_id);
				property.setAuditDetails(propertyAuditDetails);

				if (!CollectionUtils.isEmpty(property.getOwner())) {
					property.getOwner().forEach(owner -> {
						String gen_owner_id = UUID.randomUUID().toString();
						owner.setId(gen_owner_id);
						owner.setProperty_id(gen_property_id);
						owner.setOwner_id(request.getRequestInfo().getUserInfo().getId().toString());
						owner.setAuditDetails(propertyAuditDetails);

						if (!CollectionUtils.isEmpty(owner.getPayment()))
							owner.getPayment().forEach(payment -> {
								String gen_payment_id = UUID.randomUUID().toString();
								payment.setId(gen_payment_id);
								payment.setAuditDetails(propertyAuditDetails);
							});
					});
				}
			});
		}

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
//		property.setAuditDetails(propertyAuditDetails);
//
//		setIdgenIds(request);
//		enrichBoundary(request);
	}
}
