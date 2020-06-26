package org.egov.cpt.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.Address;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.CorrespondenceAddress;
import org.egov.cpt.models.Document;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyDetails;
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
				PropertyDetails propertyDetail = getPropertyDetail(property, requestInfo, gen_property_id);

				property.setId(gen_property_id);
				property.setAuditDetails(propertyAuditDetails);
				property.setPropertyDetails(propertyDetail);

				if (!CollectionUtils.isEmpty(property.getOwners())) {
					property.getOwners().forEach(owner -> {
						String gen_owner_id = UUID.randomUUID().toString();
						owner.setId(gen_owner_id);
						owner.setPropertyId(gen_property_id);
						owner.setTenantId(property.getTenantId());
						owner.setAuditDetails(propertyAuditDetails);
						OwnerDetails ownerDetails = getOwnerShipDetails(owner, property, requestInfo, gen_property_id);
						owner.setOwnerDetails(ownerDetails);

//						if (!CollectionUtils.isEmpty(owner.getPayment())) {
//							owner.getPayment().forEach(payment -> {
//								String gen_payment_id = UUID.randomUUID().toString();
//								payment.setId(gen_payment_id);
//								payment.setTenantId(property.getTenantId());
//								payment.setAuditDetails(propertyAuditDetails);
//							});
//						}
					});
				}
			});
		}

//		setIdgenIds(request);
//		enrichBoundary(request);
	}

	private OwnerDetails getOwnerShipDetails(Owner owner, Property property, RequestInfo requestInfo,
			String gen_property_id) {
		OwnerDetails ownerDetails = owner.getOwnerDetails();
		String gen_owner_details_id = UUID.randomUUID().toString();
		AuditDetails ownerAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		CorrespondenceAddress correspondenceAddress = getCorrespondenceAddress(owner, property, requestInfo,
				gen_property_id);
		ownerDetails.setId(gen_owner_details_id);
		ownerDetails.setPropertyId(property.getId());
		ownerDetails.setOwnerId(owner.getId());
		ownerDetails.setTenantId(property.getTenantId());
		ownerDetails.setCorrespondenceAddress(correspondenceAddress);
		ownerDetails.setAuditDetails(ownerAuditDetails);
		return ownerDetails;
	}

	private CorrespondenceAddress getCorrespondenceAddress(Owner owner, Property property, RequestInfo requestInfo,
			String gen_property_id) {
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		CorrespondenceAddress address = owner.getOwnerDetails().getCorrespondenceAddress();
		String gen_address_id = UUID.randomUUID().toString();
		address.setId(gen_address_id);
		address.setPropertyId(gen_property_id);
		address.setTransitNumber(property.getTransitNumber());
		address.setTenantId(property.getTenantId());
		address.setColony(property.getColony());
		address.setAuditDetails(propertyAuditDetails);
		return address;
	}

	public void enrichUpdateRequest(PropertyRequest request, List<Property> propertyFromDb) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				AuditDetails propertyAuditDetails = property.getAuditDetails();
				propertyAuditDetails.setLastModifiedBy(auditDetails.getLastModifiedBy());
				propertyAuditDetails.setLastModifiedTime(auditDetails.getLastModifiedTime());

				property.setAuditDetails(propertyAuditDetails);

				if (!CollectionUtils.isEmpty(property.getOwners())) {
					property.getOwners().forEach(owner -> {
						owner.setAuditDetails(propertyAuditDetails);

//						if (!CollectionUtils.isEmpty(owner.getPayment())) {
//							owner.getPayment().forEach(payment -> {
//								payment.setAuditDetails(propertyAuditDetails);
//							});
//						}
					});
				}
			});
		}
	}

	public PropertyDetails getPropertyDetail(Property property, RequestInfo requestInfo, String gen_property_id) {
		PropertyDetails propertyDetail = property.getPropertyDetails();
		String gen_property_details_id = UUID.randomUUID().toString();
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		propertyDetail.setId(gen_property_details_id);
		propertyDetail.setPropertyId(gen_property_id);
		propertyDetail.setTransitNumber(property.getTransitNumber());
		propertyDetail.setTenantId(property.getTenantId());

		Address address = getAddress(property, requestInfo, gen_property_id);
		propertyDetail.setAddress(address);

		List<Document> applicationDocuments = getApplicationDocs(propertyDetail, property, requestInfo,
				gen_property_id);
		propertyDetail.setApplicationDocuments(applicationDocuments);

		if (!CollectionUtils.isEmpty(property.getOwners())) {
			property.getOwners().forEach(owner -> {
				if (owner.getActiveState()) {
					propertyDetail.setCurrentOwner(owner.getOwnerDetails().getName());
				}
			});
		}

//		propertyDetail.setAdditionalDetails(property.getPropertyDetails().getAdditionalDetails());
//		JsonNode addtionalDetails = propertyDetail.getAdditionalDetails();
////		((ObjectNode)addtionalDetails).put(addtionalDetails);
//		propertyDetail.setAdditionalDetails(addtionalDetails);

//		PGobject jsonObject = new PGobject();
//		jsonObject.setType("json");
//		jsonObject.setValue(propertyDetail.getAdditionalDetails().toString());
//		propertyDetail.setAdditionalDetails(jsonObject);
		propertyDetail.setAuditDetails(propertyAuditDetails);
		return propertyDetail;
	}

	private List<Document> getApplicationDocs(PropertyDetails propertyDetails, Property property,
			RequestInfo requestInfo, String gen_property_id) {
		List<Document> applicationDocuments = propertyDetails.getApplicationDocuments();
		if (!CollectionUtils.isEmpty(applicationDocuments)) {
			AuditDetails docAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
			applicationDocuments.forEach(document -> {
				String gen_doc_id = UUID.randomUUID().toString();
				document.setId(gen_doc_id);
				document.setPropertyId(gen_property_id);
				document.setTransitNumber(property.getTransitNumber());
				document.setTenantId(property.getTenantId());
				document.setAuditDetails(docAuditDetails);
			});
		}
		return applicationDocuments;
	}

	public Address getAddress(Property property, RequestInfo requestInfo, String gen_property_id) {
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		Address address = property.getPropertyDetails().getAddress();
		String gen_address_id = UUID.randomUUID().toString();
		address.setId(gen_address_id);
		address.setPropertyId(gen_property_id);
		address.setTransitNumber(property.getTransitNumber());
		address.setTenantId(property.getTenantId());
		address.setColony(property.getColony());
		address.setAuditDetails(propertyAuditDetails);
		return address;
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
			});
		});
	}
}
