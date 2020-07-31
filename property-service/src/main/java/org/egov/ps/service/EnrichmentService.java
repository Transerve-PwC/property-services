package org.egov.ps.service;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.PurchaseDetails;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.AuditDetails;
import org.egov.ps.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrichmentService {

	@Autowired
	Util util;

	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails propertyAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {

				String gen_property_id = UUID.randomUUID().toString();
				PropertyDetails propertyDetail = getPropertyDetail(property, requestInfo, gen_property_id);

				property.setId(gen_property_id);
				property.setAuditDetails(propertyAuditDetails);
				property.setPropertyDetails(propertyDetail);

			});
		}
	}

	public PropertyDetails getPropertyDetail(Property property, RequestInfo requestInfo, String gen_property_id) {

		PropertyDetails propertyDetail = property.getPropertyDetails();
		String gen_property_details_id = UUID.randomUUID().toString();
		AuditDetails propertyDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		List<Owner> owners = getOwners(property, requestInfo, gen_property_details_id);
		List<CourtCase> courtCases = getCourtCases(property, requestInfo, gen_property_details_id);
		List<PurchaseDetails> purchaseDetails = getPurchaseDetails(property, requestInfo, gen_property_details_id);

		propertyDetail.setId(gen_property_details_id);
		propertyDetail.setTenantId(property.getTenantId());
		propertyDetail.setPropertyId(gen_property_id);
		propertyDetail.setAuditDetails(propertyDetailsAuditDetails);
		propertyDetail.setOwners(owners);
		propertyDetail.setCourtCases(courtCases);
		propertyDetail.setPurchaseDetails(purchaseDetails);

		return propertyDetail;
	}

	private List<Owner> getOwners(Property property, RequestInfo requestInfo, String gen_property_details_id) {

		List<Owner> owners = property.getPropertyDetails().getOwners();

		if (!CollectionUtils.isEmpty(owners)) {

			AuditDetails ownerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			owners.forEach(owner -> {

				String gen_owner_id = UUID.randomUUID().toString();
				OwnerDetails ownerDetails = getOwnerDetail(property, owner, requestInfo, gen_owner_id);

				owner.setId(gen_owner_id);
				owner.setTenantId(property.getTenantId());
				owner.setPropertyDetailsId(gen_property_details_id);
				owner.setOwnerDetails(ownerDetails);
				owner.setAuditDetails(ownerAuditDetails);

			});
		}
		return owners;
	}

	public OwnerDetails getOwnerDetail(Property property, Owner owner, RequestInfo requestInfo, String gen_owner_id) {

		OwnerDetails ownerDetails = owner.getOwnerDetails();
		String gen_owner_details_id = UUID.randomUUID().toString();
		AuditDetails ownerDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		ownerDetails.setId(gen_owner_details_id);
		ownerDetails.setTenantId(property.getTenantId());
		ownerDetails.setOwnerId(gen_owner_id);
		ownerDetails.setAuditDetails(ownerDetailsAuditDetails);

		return ownerDetails;
	}

	private List<CourtCase> getCourtCases(Property property, RequestInfo requestInfo, String gen_property_details_id) {

		List<CourtCase> courtCases = property.getPropertyDetails().getCourtCases();

		if (!CollectionUtils.isEmpty(courtCases)) {

			AuditDetails courtCaseAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			courtCases.forEach(courtCase -> {

				String gen_court_case_id = UUID.randomUUID().toString();

				courtCase.setId(gen_court_case_id);
				courtCase.setTenantId(property.getTenantId());
				courtCase.setPropertyDetailsId(gen_property_details_id);
				courtCase.setAuditDetails(courtCaseAuditDetails);

			});
		}
		return courtCases;
	}

	private List<PurchaseDetails> getPurchaseDetails(Property property, RequestInfo requestInfo,
			String gen_property_details_id) {

		List<PurchaseDetails> purchaseDetails = property.getPropertyDetails().getPurchaseDetails();

		if (!CollectionUtils.isEmpty(purchaseDetails)) {

			AuditDetails purchaseDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			purchaseDetails.forEach(purchaseDetail -> {

				String gen_purchase_details_id = UUID.randomUUID().toString();

				purchaseDetail.setId(gen_purchase_details_id);
				purchaseDetail.setTenantId(property.getTenantId());
				purchaseDetail.setPropertyDetailsId(gen_property_details_id);
				purchaseDetail.setAuditDetails(purchaseDetailsAuditDetails);

			});
		}
		return purchaseDetails;
	}

}
