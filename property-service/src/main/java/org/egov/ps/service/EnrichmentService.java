package org.egov.ps.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Document;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Payment;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.PurchaseDetails;
import org.egov.ps.util.PSConstants;
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
				property.setState(PSConstants.PM_DRAFTED);

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

		List<Payment> paymentDetails = getPaymentDetails(property, owner, requestInfo, gen_owner_details_id);

		ownerDetails.setId(gen_owner_details_id);
		ownerDetails.setTenantId(property.getTenantId());
		ownerDetails.setOwnerId(gen_owner_id);
		ownerDetails.setPaymentDetails(paymentDetails);
		ownerDetails.setAuditDetails(ownerDetailsAuditDetails);

		return ownerDetails;
	}

	private List<Payment> getPaymentDetails(Property property, Owner owner, RequestInfo requestInfo,
			String gen_owner_details_id) {

		List<Payment> paymentDetails = owner.getOwnerDetails().getPaymentDetails();

		if (!CollectionUtils.isEmpty(paymentDetails)) {

			AuditDetails paymentDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			paymentDetails.forEach(paymentDetail -> {

				String gen_payment_detail_id = UUID.randomUUID().toString();

				paymentDetail.setId(gen_payment_detail_id);
				paymentDetail.setTenantId(property.getTenantId());
				paymentDetail.setOwnerDetailsId(gen_owner_details_id);
				paymentDetail.setAuditDetails(paymentDetailsAuditDetails);

			});
		}
		return paymentDetails;
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

	public void enrichUpdateRequest(PropertyRequest request, List<Property> propertyFromDb) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				AuditDetails modifyAuditDetails = property.getAuditDetails();
				modifyAuditDetails.setLastModifiedBy(auditDetails.getLastModifiedBy());
				modifyAuditDetails.setLastModifiedTime(auditDetails.getLastModifiedTime());
				property.setAuditDetails(modifyAuditDetails);
				property.getPropertyDetails().setAuditDetails(modifyAuditDetails);

				PropertyDetails propertyDetail = updatePropertyDetail(property, requestInfo);
				property.setPropertyDetails(propertyDetail);

				if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
					property.getPropertyDetails().getOwners().forEach(owner -> {
						owner.setAuditDetails(modifyAuditDetails);
						owner.getOwnerDetails().setAuditDetails(modifyAuditDetails);
					});
				}
			});
		}
	}

	private PropertyDetails updatePropertyDetail(Property property, RequestInfo requestInfo) {
		PropertyDetails propertyDetail = property.getPropertyDetails();
		List<Document> ownerDocuments = updateOwnerDocs(propertyDetail, property, requestInfo);
		List<Payment> paymentDetails = updatePaymentDetails(propertyDetail, property, requestInfo);
		propertyDetail.getOwners().forEach(owner -> {
			owner.getOwnerDetails().setOwnerDocuments(ownerDocuments);
		});
		return propertyDetail;
	}

	private List<Document> updateOwnerDocs(PropertyDetails propertyDetail, Property property, RequestInfo requestInfo) {
		List<Document> ownerDocs = new ArrayList<>();
		propertyDetail.getOwners().forEach(owner -> {
			List<Document> ownerDocuments = owner.getOwnerDetails().getOwnerDocuments();
			if (!CollectionUtils.isEmpty(ownerDocuments)) {
				AuditDetails docAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
				ownerDocuments.forEach(document -> {
					if (document.getId() == null) {
						String gen_doc_id = UUID.randomUUID().toString();
						document.setId(gen_doc_id);
						document.setTenantId(property.getTenantId());
						document.setReferenceId(owner.getOwnerDetails().getId());
						document.setPropertyId(property.getId());
					}
					document.setAuditDetails(docAuditDetails);
				});
			}
			ownerDocs.addAll(ownerDocuments);
		});
		return ownerDocs;
	}

	private List<Payment> updatePaymentDetails(PropertyDetails propertyDetail, Property property,
			RequestInfo requestInfo) {
		List<Payment> paymentDetails = new ArrayList<>();
		propertyDetail.getOwners().forEach(owner -> {
			List<Payment> payments = owner.getOwnerDetails().getPaymentDetails();
			if (!CollectionUtils.isEmpty(payments)) {
				AuditDetails paymentAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
				payments.forEach(payment -> {
					if (payment.getId() == null) {
						String gen_payment_detail_id = UUID.randomUUID().toString();
						payment.setId(gen_payment_detail_id);
						payment.setTenantId(property.getTenantId());
						payment.setOwnerDetailsId(owner.getOwnerDetails().getId());
						payment.setAuditDetails(paymentAuditDetails);
					}
					payment.setAuditDetails(paymentAuditDetails);
				});
			}
			paymentDetails.addAll(payments);
		});
		return paymentDetails;
	}

}
