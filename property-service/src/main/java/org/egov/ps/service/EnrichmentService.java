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
import org.egov.ps.model.idgen.IdResponse;
import org.egov.ps.repository.IdGenRepository;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.AuditDetails;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class EnrichmentService {

	@Autowired
	Util util;

	@Autowired
	IdGenRepository idGenRepository;

	@Autowired
	private Configuration config;

	@Autowired
	private MDMSService mdmsservice;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private ObjectMapper objectMapper;

	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails propertyAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {

				String gen_property_id = UUID.randomUUID().toString();
				PropertyDetails propertyDetail = getPropertyDetail(property, requestInfo, gen_property_id);

				property.setId(gen_property_id);
				property.setPropertyDetails(propertyDetail);
				property.setState(PSConstants.PM_DRAFTED);
				property.setAuditDetails(propertyAuditDetails);

			});
		}
	}

	public PropertyDetails getPropertyDetail(Property property, RequestInfo requestInfo, String gen_property_id) {

		PropertyDetails propertyDetail = property.getPropertyDetails();
		String gen_property_details_id = UUID.randomUUID().toString();

		List<CourtCase> courtCases = getCourtCases(property, requestInfo, gen_property_details_id);
		List<Payment> paymentDetails = createUpdatePaymentDetails(property, requestInfo);

		AuditDetails propertyDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		List<Owner> owners = getOwners(property, requestInfo, gen_property_details_id, gen_property_id);

		propertyDetail.setId(gen_property_details_id);
		propertyDetail.setTenantId(property.getTenantId());
		propertyDetail.setPropertyId(gen_property_id);
		propertyDetail.setOwners(owners);
		propertyDetail.setCourtCases(courtCases);
		propertyDetail.setPaymentDetails(paymentDetails);
		propertyDetail.setAuditDetails(propertyDetailsAuditDetails);

		return propertyDetail;
	}

	private List<Owner> getOwners(Property property, RequestInfo requestInfo, String gen_property_details_id,
			String gen_property_id) {

		List<Owner> owners = property.getPropertyDetails().getOwners();

		if (!CollectionUtils.isEmpty(owners)) {

			AuditDetails ownerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			owners.forEach(owner -> {

				String gen_owner_id = UUID.randomUUID().toString();
				OwnerDetails ownerDetails = getOwnerDetail(property, owner, requestInfo, gen_owner_id, gen_property_id);

				owner.setId(gen_owner_id);
				owner.setTenantId(property.getTenantId());
				owner.setPropertyDetailsId(gen_property_details_id);
//				owner.setSerialNumber(""); TODO: Whether sr.no will be generated from BackEnd or FrontEnd
				owner.setOwnerDetails(ownerDetails);
				owner.setAuditDetails(ownerAuditDetails);

			});
		}
		return owners;
	}

	public OwnerDetails getOwnerDetail(Property property, Owner owner, RequestInfo requestInfo, String gen_owner_id,
			String gen_property_id) {

		OwnerDetails ownerDetails = owner.getOwnerDetails();
		String gen_owner_details_id = UUID.randomUUID().toString();
		AuditDetails ownerDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		List<Document> ownerDocuments = createUpdateOwnerDocs(property, requestInfo, gen_owner_details_id,
				gen_property_id);

		ownerDetails.setId(gen_owner_details_id);
		ownerDetails.setTenantId(property.getTenantId());
		ownerDetails.setOwnerId(gen_owner_id);
		ownerDetails.setOwnerDocuments(ownerDocuments);
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

	public void enrichUpdateRequest(PropertyRequest request, List<Property> propertyFromDb) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {

				PropertyDetails propertyDetail = updatePropertyDetail(property, requestInfo, auditDetails);
				property.setPropertyDetails(propertyDetail);
				property.setAuditDetails(auditDetails);

			});
		}
	}

	private PropertyDetails updatePropertyDetail(Property property, RequestInfo requestInfo,
			AuditDetails auditDetails) {
		PropertyDetails propertyDetail = property.getPropertyDetails();
		List<Owner> owners = updateOwners(property, requestInfo);
		List<Payment> paymentDetails = createUpdatePaymentDetails(property, requestInfo);

		propertyDetail.setOwners(owners);
		propertyDetail.setPaymentDetails(paymentDetails);
		propertyDetail.setAuditDetails(auditDetails);

		return propertyDetail;
	}

	private List<Owner> updateOwners(Property property, RequestInfo requestInfo) {
		List<Owner> owners = property.getPropertyDetails().getOwners();
		AuditDetails ownerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
		if (!CollectionUtils.isEmpty(owners)) {
			owners.forEach(owner -> {
				OwnerDetails ownerDetails = updateOwnerDetail(property, owner, requestInfo);
				owner.setOwnerDetails(ownerDetails);
				owner.setAuditDetails(ownerAuditDetails);
			});
		}

		return owners;
	}

	private OwnerDetails updateOwnerDetail(Property property, Owner owner, RequestInfo requestInfo) {

		OwnerDetails ownerDetails = owner.getOwnerDetails();
		AuditDetails ownerDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);

		List<Document> ownerDocuments = createUpdateOwnerDocs(property, requestInfo, ownerDetails.getId(),
				property.getId());
//		List<CourtCase> courtCases = updateCourtCases(owner, requestInfo); TODO: Confirm that court details are not updated again

		ownerDetails.setOwnerDocuments(ownerDocuments);
//		ownerDetails.setCourtCases(courtCases); TODO: Confirm that court details are not updated again

		ownerDetails.setAuditDetails(ownerDetailsAuditDetails);

		return ownerDetails;
	}

	private List<Document> createUpdateOwnerDocs(Property property, RequestInfo requestInfo, String reference_id,
			String gen_property_id) {
		List<Document> ownerDocs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
			property.getPropertyDetails().getOwners().forEach(owner -> {
				List<Document> ownerDocuments = owner.getOwnerDetails().getOwnerDocuments();
				if (!CollectionUtils.isEmpty(ownerDocuments)) {
					AuditDetails docAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
					ownerDocuments.forEach(document -> {
						if (document.getId() == null) {
							String gen_doc_id = UUID.randomUUID().toString();
							document.setId(gen_doc_id);
							document.setTenantId(property.getTenantId());
							document.setReferenceId(reference_id);
							document.setPropertyId(gen_property_id);
							document.setAuditDetails(docAuditDetails);
						}
						document.setAuditDetails(docAuditDetails);
					});
					ownerDocs.addAll(ownerDocuments);
				}
			});
		}
		return ownerDocs;
	}

	private List<Payment> createUpdatePaymentDetails(Property property, RequestInfo requestInfo) {
		List<Payment> paymentDetails = new ArrayList<>();
		if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
			property.getPropertyDetails().getOwners().forEach(owner -> {
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
		}
		return paymentDetails;
	}

	public void enrichCreateApplication(ApplicationRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		List<Application> applications = request.getApplications();

		if (!CollectionUtils.isEmpty(applications)) {
			applications.forEach(application -> {
				String gen_application_id = UUID.randomUUID().toString();
				AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
				JsonNode applicationDetails = enrichApplicationDetails(application);
				enrichPropertyDetails(application);

				application.setId(gen_application_id);
				application.setAuditDetails(auditDetails);
				application.setApplicationDetails(applicationDetails);
			});
			setIdgenIds(request);
		}
	}

	private JsonNode enrichApplicationDetails(Application application) {
		JsonNode applicationDetails = application.getApplicationDetails();

		JsonNode transferor = (applicationDetails.get("transferor") != null) ? applicationDetails.get("transferor")
				: applicationDetails.get("owner");

		String propertyId = application.getProperty().getId();
		String transferorId = transferor.get("id").asText();

		Property property = propertyRepository.findPropertyById(propertyId);
		if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
			property.getPropertyDetails().getOwners().forEach(owner -> {
				if (owner.getId().equals(transferorId)) {
					((ObjectNode) transferor).put("serialNumber", owner.getSerialNumber());
					((ObjectNode) transferor).put("share", owner.getShare());
					((ObjectNode) transferor).put("cpNumber", owner.getCpNumber());

					ObjectNode ownerDetails = objectMapper.createObjectNode();
					ownerDetails.put("ownerName", owner.getOwnerDetails().getOwnerName());
					ownerDetails.put("guardianName", owner.getOwnerDetails().getGuardianName());
					ownerDetails.put("guardianRelation", owner.getOwnerDetails().getGuardianRelation());
					ownerDetails.put("mobileNumber", owner.getOwnerDetails().getMobileNumber());
					ownerDetails.put("allotmentNumber", owner.getOwnerDetails().getAllotmentNumber());
					ownerDetails.put("dateOfAllotment", owner.getOwnerDetails().getDateOfAllotment());
					ownerDetails.put("possesionDate", owner.getOwnerDetails().getPossesionDate());
					ownerDetails.put("isApproved", owner.getOwnerDetails().getIsApproved());
					ownerDetails.put("isCurrentOwner", owner.getOwnerDetails().getIsCurrentOwner());
					ownerDetails.put("isMasterEntry", owner.getOwnerDetails().getIsMasterEntry());
					ownerDetails.put("address", owner.getOwnerDetails().getAddress());

					((ObjectNode) transferor).set("transferorDetails", ownerDetails);
				}
			});
		}

		if (applicationDetails.get("transferee") != null && applicationDetails.get("transferee").get("id") != null) {
			JsonNode transferee = applicationDetails.get("transferee");
			String transfereeId = transferee.get("id").asText();

			if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
				property.getPropertyDetails().getOwners().forEach(owner -> {
					if (owner.getId().equals(transfereeId)) {
						((ObjectNode) transferee).put("name", owner.getOwnerDetails().getOwnerName());
						((ObjectNode) transferee).put("fatherOrHusbandName", owner.getOwnerDetails().getGuardianName());
						((ObjectNode) transferee).put("relation", owner.getOwnerDetails().getGuardianRelation());
						((ObjectNode) transferee).put("address", owner.getOwnerDetails().getAddress());
						((ObjectNode) transferee).put("relationWithDeceased",
								owner.getOwnerDetails().getGuardianRelation());
						((ObjectNode) transferee).put("mobileNo", owner.getOwnerDetails().getMobileNumber());
					}
				});
			}
		}
		return applicationDetails;
	}

	private void enrichPropertyDetails(Application application) {
		Property property = propertyRepository.findPropertyById(application.getProperty().getId());
		Property propertyToEnrich = application.getProperty();

		propertyToEnrich.setFileNumber(property.getFileNumber());
		propertyToEnrich.setTenantId(property.getTenantId());
		propertyToEnrich.setCategory(property.getCategory());
		propertyToEnrich.setSubCategory(property.getSubCategory());
		propertyToEnrich.setSiteNumber(property.getSiteNumber());
		propertyToEnrich.setSectorNumber(property.getSectorNumber());

		PropertyDetails propertyDetails = new PropertyDetails();
		propertyDetails.setBranchType(property.getPropertyDetails().getBranchType());
		propertyDetails.setPropertyType(property.getPropertyDetails().getBranchType());
		propertyDetails.setTypeOfAllocation(property.getPropertyDetails().getBranchType());
		propertyDetails.setEmdAmount(property.getPropertyDetails().getEmdAmount());
		propertyDetails.setEmdDate(property.getPropertyDetails().getEmdDate());
		propertyDetails.setModeOfAuction(property.getPropertyDetails().getModeOfAuction());
		propertyDetails.setSchemeName(property.getPropertyDetails().getSchemeName());
		propertyDetails.setDateOfAuction(property.getPropertyDetails().getDateOfAuction());
		propertyDetails.setAreaSqft(property.getPropertyDetails().getAreaSqft());
		propertyDetails.setRatePerSqft(property.getPropertyDetails().getRatePerSqft());
		propertyDetails.setLastNocDate(property.getPropertyDetails().getLastNocDate());
		propertyDetails.setServiceCategory(property.getPropertyDetails().getServiceCategory());
		propertyDetails.setIsPropertyActive(property.getPropertyDetails().getIsPropertyActive());
		propertyDetails.setTradeType(property.getPropertyDetails().getTradeType());
		propertyDetails.setCompanyName(property.getPropertyDetails().getCompanyName());
		propertyDetails.setCompanyAddress(property.getPropertyDetails().getCompanyAddress());
		propertyDetails.setCompanyRegistrationNumber(property.getPropertyDetails().getCompanyRegistrationNumber());
		propertyDetails.setCompanyType(property.getPropertyDetails().getCompanyType());

		propertyToEnrich.setPropertyDetails(propertyDetails);
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

	public void enrichMortgageDetailsRequest(PropertyRequest request) {
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
					property.getPropertyDetails().getOwners().forEach(owner -> {
						// checking - owner is existing and mortgage details bound with user.
						if (null != owner.getId() && !owner.getId().isEmpty() && null != owner.getMortgageDetails()) {
							// validate mortgage details - documents
							validateMortgageDetails(property, owner, request.getRequestInfo(), owner.getId());
							owner.setMortgageDetails(
									getMortgage(property, owner, request.getRequestInfo(), owner.getId()));
						}
					});
				}
			});
		}
	}

	public void validateMortgageDetails(Property property, Owner owner, RequestInfo requestInfo, String id) {
		MortgageDetails mortgage = owner.getMortgageDetails();
		List<Map<String, Object>> fieldConfigurations = mdmsservice
				.getMortgageDocumentConfig(mortgage.getMortgageType(), requestInfo, property.getTenantId());

		// TODO :: write code to validate documents base on master json template.
	}

	private MortgageDetails getMortgage(Property property, Owner owner, RequestInfo requestInfo, String gen_owner_id) {
		String gen_mortgage_id = UUID.randomUUID().toString();

		MortgageDetails mortgage = owner.getMortgageDetails();
		mortgage.setId(gen_mortgage_id);
		mortgage.setTenantId(property.getTenantId());
		mortgage.setOwnerId(gen_owner_id);

		return mortgage;
	}

}
