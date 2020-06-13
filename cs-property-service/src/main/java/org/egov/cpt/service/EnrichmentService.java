package org.egov.cpt.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.UserDetailResponse;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
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
				property.setPropertyId(gen_property_id);
				property.setAuditDetails(propertyAuditDetails);

				if (!CollectionUtils.isEmpty(property.getOwners())) {
					property.getOwners().forEach(owner -> {
						String gen_owner_id = UUID.randomUUID().toString();
						owner.setId(gen_owner_id);
						owner.setPropertyId(gen_property_id);
						owner.setOwnerId(request.getRequestInfo().getUserInfo().getId().toString());
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

	/**
	 * Populates the owner fields inside of property objects from the response got
	 * from calling user api
	 * 
	 * @param userDetailResponse response from user api which contains list of user
	 *                           which are used to populate owners in properties
	 * @param properties         List of property whose owner's are to be populated
	 *                           from userDetailResponse
	 */
	public void enrichOwner(UserDetailResponse userDetailResponse, List<Property> properties) {

		List<Owner> users = userDetailResponse.getUser();
		Map<String, Owner> userIdToOwnerMap = new HashMap<>();
		users.forEach(user -> userIdToOwnerMap.put(user.getId(), user));

		properties.forEach(property -> {

			property.getOwners().forEach(owner -> {

				if (userIdToOwnerMap.get(owner.getId()) == null)
					throw new CustomException("OWNER SEARCH ERROR",
							"The owner of the propertyDetail " + property.getId() + " is not coming in user search");
//				else
//					owner.addUserDetail(userIdToOwnerMap.get(owner.getId()));
			});
		});
	}
}
