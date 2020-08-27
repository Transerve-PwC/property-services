package org.egov.ps.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Document;
import org.egov.ps.model.MortgageDetails;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Payment;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.PurchaseDetails;
import org.egov.ps.model.idgen.IdResponse;
import org.egov.ps.repository.IdGenRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.AuditDetails;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EnrichmentService {

	@Autowired
	Util util;

	@Autowired
	IdGenRepository idGenRepository;

	@Autowired
	private Configuration config;

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
	
	public void enrichMortgageDetailsRequest(PropertyRequest request) {
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				property.getPropertyDetails().getOwners().forEach(owner ->{
					owner.setMortgageDetails(getMortgage(property, owner, request.getRequestInfo(), owner.getId()));
				});
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
	
	private MortgageDetails getMortgage(Property property, Owner owner, RequestInfo requestInfo, String gen_owner_id) {
		String gen_mortgage_id = UUID.randomUUID().toString();
		
		MortgageDetails mortgage = owner.getMortgageDetails();
		mortgage.setId(gen_mortgage_id);
		mortgage.setTenantId(property.getTenantId());
		mortgage.setOwnerId(gen_owner_id);
		
		return mortgage;
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
						if (owner.getId() == null) {
							List<Owner> ownerUpdate = getOwners(property, requestInfo, propertyDetail.getId());
							propertyDetail.setOwners(ownerUpdate);
						} else {
							owner.setAuditDetails(modifyAuditDetails);
							owner.getOwnerDetails().setAuditDetails(modifyAuditDetails);
						}
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
			owner.getOwnerDetails().setPaymentDetails(paymentDetails);
		});

		if (!CollectionUtils.isEmpty(propertyDetail.getCourtCases())) {
			propertyDetail.getCourtCases().forEach(courtCase -> {
				if (courtCase.getId() == null) {
					List<CourtCase> courtCases = getCourtCases(property, requestInfo, propertyDetail.getId());
					propertyDetail.setCourtCases(courtCases);
				}
			});
		}

		if (!CollectionUtils.isEmpty(propertyDetail.getPurchaseDetails())) {
			propertyDetail.getPurchaseDetails().forEach(purchaseDetail -> {
				if (purchaseDetail.getId() == null) {
					List<PurchaseDetails> purchaseDetails = getPurchaseDetails(property, requestInfo,
							propertyDetail.getId());
					propertyDetail.setPurchaseDetails(purchaseDetails);
				}
			});
		}

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
				ownerDocs.addAll(ownerDocuments);
			}
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
				paymentDetails.addAll(payments);
			}
		});
		return paymentDetails;
	}

	public void enrichCreateApplication(ApplicationRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		List<Application> applications = request.getApplications();
		if (!CollectionUtils.isEmpty(applications)) {

			applications.forEach(application -> {
				String gen_application_id = UUID.randomUUID().toString();
				AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

				application.setId(gen_application_id);
				application.setAuditDetails(auditDetails);
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
	private void setIdgenIds(ApplicationRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String tenantId = request.getApplications().get(0).getTenantId();
		List<Application> applications = request.getApplications();
		int size = request.getApplications().size();

		List<String> applicationNumbers = setIdgenIds(requestInfo, tenantId, size,
				config.getApplicationNumberIdgenNamePS(), config.getApplicationNumberIdgenFormatPS());
		ListIterator<String> itr = applicationNumbers.listIterator();

		if (!CollectionUtils.isEmpty(applications)) {
			applications.forEach(application -> {
				application.setApplicationNumber(itr.next());
			});
		}
	}

	private List<String> setIdgenIds(RequestInfo requestInfo, String tenantId, int size, String idGenName,
			String idGenFormate) {
		List<String> applicationNumbers = null;

		applicationNumbers = getIdList(requestInfo, tenantId, idGenName, idGenFormate, size);

		Map<String, String> errorMap = new HashMap<>();
		if (applicationNumbers.size() != size) {
			errorMap.put("IDGEN ERROR ",
					"The number of application number returned by idgen is not equal to number of Applications");
		}
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		return applicationNumbers;
	}

	public void enrichUpdateApplication(ApplicationRequest request) {
		
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getApplications())) {
			request.getApplications().forEach(application -> {
				AuditDetails modifyAuditDetails = application.getAuditDetails();
				modifyAuditDetails.setLastModifiedBy(auditDetails.getLastModifiedBy());
				modifyAuditDetails.setLastModifiedTime(auditDetails.getLastModifiedTime());
				application.setAuditDetails(modifyAuditDetails);
				
				List<Document> applicationDocs = new ArrayList<>();
				List<Document> applicationDocuments = application.getApplicationDocuments();
				if (!CollectionUtils.isEmpty(applicationDocuments)) {
					AuditDetails docAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
					applicationDocuments.forEach(document -> {
						if (document.getId() == null) {
							String gen_doc_id = UUID.randomUUID().toString();
							document.setId(gen_doc_id);
							document.setTenantId(application.getTenantId());
							document.setReferenceId(application.getId());
							document.setPropertyId(application.getProperty().getId());
						}
						document.setAuditDetails(docAuditDetails);
					});
					applicationDocs.addAll(applicationDocuments);
				}
			});
		}
	}

}
