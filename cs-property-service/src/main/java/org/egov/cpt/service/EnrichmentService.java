package org.egov.cpt.service;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Address;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Document;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.OwnerDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyDetails;
import org.egov.cpt.models.UserDetailResponse;
import org.egov.cpt.models.Idgen.IdResponse;
import org.egov.cpt.repository.IdGenRepository;
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

	@Autowired
	IdGenRepository idGenRepository;

	@Autowired
	private PropertyConfiguration config;

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
					});
				}
			});
		}
	}

	private OwnerDetails getOwnerShipDetails(Owner owner, Property property, RequestInfo requestInfo,
			String gen_property_id) {
		OwnerDetails ownerDetails = owner.getOwnerDetails();
		String gen_owner_details_id = UUID.randomUUID().toString();
		AuditDetails ownerAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		ownerDetails.setId(gen_owner_details_id);
		ownerDetails.setPropertyId(property.getId());
		ownerDetails.setOwnerId(owner.getId());
		ownerDetails.setTenantId(property.getTenantId());
		ownerDetails.setAuditDetails(ownerAuditDetails);
		return ownerDetails;
	}

	private Address getCorrespondenceAddress(Owner owner, Property property, RequestInfo requestInfo,
			String gen_property_id) {
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		Address address = owner.getOwnerDetails().getCorrespondenceAddress();
		if (address != null) {
			String gen_address_id = UUID.randomUUID().toString();
			address.setId(gen_address_id);
			address.setPropertyId(gen_property_id);
			address.setTransitNumber(property.getTransitNumber());
			address.setTenantId(property.getTenantId());
			address.setColony(property.getColony());
			address.setAuditDetails(propertyAuditDetails);
			return address;
		}
		
		return address;
	}

	public void enrichUpdateRequest(PropertyRequest request, List<Property> propertyFromDb) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				AuditDetails modifyAuditDetails = property.getAuditDetails();
				modifyAuditDetails.setLastModifiedBy(auditDetails.getLastModifiedBy());
				modifyAuditDetails.setLastModifiedTime(auditDetails.getLastModifiedTime());
				property.setAuditDetails(modifyAuditDetails);
				property.getPropertyDetails().setAuditDetails(modifyAuditDetails);

				PropertyDetails propertyDetail = updatePropertyDetail(property, requestInfo);
				property.setPropertyDetails(propertyDetail);

				if (!CollectionUtils.isEmpty(property.getOwners())) {
					property.getOwners().forEach(owner -> {
						owner.setAuditDetails(modifyAuditDetails);
						owner.getOwnerDetails().setAuditDetails(modifyAuditDetails);
					});
				}
			});
		}
	}

	public PropertyDetails updatePropertyDetail(Property property, RequestInfo requestInfo) {
		PropertyDetails propertyDetail = property.getPropertyDetails();
		List<Document> applicationDocuments = updateApplicationDocs(propertyDetail, property, requestInfo);
		propertyDetail.setApplicationDocuments(applicationDocuments);
		return propertyDetail;
	}

	private List<Document> updateApplicationDocs(PropertyDetails propertyDetails, Property property,
			RequestInfo requestInfo) {
		List<Document> applicationDocuments = propertyDetails.getApplicationDocuments();
		if (!CollectionUtils.isEmpty(applicationDocuments)) {
			AuditDetails docAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
			applicationDocuments.forEach(document -> {
				if (document.getId() == null) {
					String gen_doc_id = UUID.randomUUID().toString();
					document.setId(gen_doc_id);
					document.setPropertyId(property.getId());
					document.setTenantId(property.getTenantId());
				}
				document.setAuditDetails(docAuditDetails);
			});
		}
		return applicationDocuments;
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

	/*
	 * Ownership Transfer
	 */
	public void enrichCreateOwnershipTransfer(PropertyRequest request) {
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
					});
				}
			});
			setIdgenIds(request);
		}
	}

	/**
	 * Returns a list of numbers generated from idgen
	 *
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idKey       code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	/**
	 * Sets the ApplicationNumber for given TradeLicenseRequest
	 *
	 * @param request TradeLicenseRequest which is to be created
	 */
	private void setIdgenIds(PropertyRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String tenantId = request.getProperties().get(0).getTenantId();
		List<Property> properties = request.getProperties();
		int peopertiesSize = request.getProperties().size();

		List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNumberIdgenNameRP(),
				config.getApplicationNumberIdgenFormatRP(), peopertiesSize);

		ListIterator<String> itr = applicationNumbers.listIterator();

		Map<String, String> errorMap = new HashMap<>();
		if (applicationNumbers.size() != request.getProperties().size()) {
			errorMap.put("IDGEN ERROR ",
					"The number of LicenseNumber returned by idgen is not equal to number of TradeLicenses");
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		if (!CollectionUtils.isEmpty(properties)) {
			properties.forEach(property -> {
				if (!CollectionUtils.isEmpty(property.getOwners())) {
					property.getOwners().forEach(owner -> {
						owner.getOwnerDetails().setApplicationNumber(itr.next());
					});
				}
			});
		}
	}
}
