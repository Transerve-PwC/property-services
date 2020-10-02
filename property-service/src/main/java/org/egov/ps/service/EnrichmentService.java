package org.egov.ps.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.model.Auction;
import org.egov.ps.model.CourtCase;
import org.egov.ps.model.Document;
import org.egov.ps.model.ExcelSearchCriteria;
import org.egov.ps.model.MortgageDetails;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Payment;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyDetails;
import org.egov.ps.model.calculation.Calculation;
import org.egov.ps.model.calculation.Category;
import org.egov.ps.model.calculation.TaxHeadEstimate;
import org.egov.ps.model.idgen.IdResponse;
import org.egov.ps.repository.IdGenRepository;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.util.FileStoreUtils;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.ps.web.contracts.AuctionTransactionRequest;
import org.egov.ps.web.contracts.AuditDetails;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	
	@Autowired
	private ReadExcelService readExcelService;

	@Autowired
	private FileStoreUtils fileStoreUtils;

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

		AuditDetails propertyDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		propertyDetail.setId(gen_property_details_id);
		propertyDetail.setTenantId(property.getTenantId());
		propertyDetail.setPropertyId(gen_property_id);
		propertyDetail.setAuditDetails(propertyDetailsAuditDetails);

		return propertyDetail;
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
		List<Owner> owners = getOwners(property, requestInfo);
		List<CourtCase> courtCases = getCourtCases(property, requestInfo); // TODO:
		List<Payment> paymentDetails = getPaymentDetails(property, requestInfo);

		propertyDetail.setOwners(owners);
		propertyDetail.setCourtCases(courtCases); // TODO: Confirm that court details are not updated again
		propertyDetail.setPaymentDetails(paymentDetails);

		propertyDetail.setAuditDetails(auditDetails);

		return propertyDetail;
	}

	private List<Owner> getOwners(Property property, RequestInfo requestInfo) {

		List<Owner> owners = property.getPropertyDetails().getOwners();

		if (!CollectionUtils.isEmpty(owners)) {

			owners.forEach(owner -> {

				if (owner.getId() == null || owner.getId().isEmpty()) {

					AuditDetails ownerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

					String gen_owner_id = UUID.randomUUID().toString();

					owner.setId(gen_owner_id);
					owner.setTenantId(property.getTenantId());
					owner.setPropertyDetailsId(property.getPropertyDetails().getId());

					OwnerDetails ownerDetails = getOwnerDetail(property, owner, requestInfo);

//				    owner.setSerialNumber(""); TODO: Whether sr.no will be generated from BackEnd or FrontEnd
					owner.setOwnerDetails(ownerDetails);
					owner.setAuditDetails(ownerAuditDetails);
				} else {

					OwnerDetails ownerDetails = getOwnerDetail(property, owner, requestInfo);
					owner.setOwnerDetails(ownerDetails);

					AuditDetails ownerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
					owner.setAuditDetails(ownerAuditDetails);
				}

			});
		}
		return owners;
	}

	public OwnerDetails getOwnerDetail(Property property, Owner owner, RequestInfo requestInfo) {

		OwnerDetails ownerDetails = owner.getOwnerDetails();

		if (ownerDetails.getId() == null || ownerDetails.getId().isEmpty()) {

			AuditDetails ownerDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

			String gen_owner_details_id = UUID.randomUUID().toString();

			ownerDetails.setId(gen_owner_details_id);
			ownerDetails.setTenantId(property.getTenantId());
			ownerDetails.setOwnerId(owner.getId());
			ownerDetails.setAuditDetails(ownerDetailsAuditDetails);
		} else {
			List<Document> ownerDocuments = getOwnerDocs(property, requestInfo);
			ownerDetails.setOwnerDocuments(ownerDocuments);

			AuditDetails ownerDetailsAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
			ownerDetails.setAuditDetails(ownerDetailsAuditDetails);
		}

		return ownerDetails;
	}

	private List<Document> getOwnerDocs(Property property, RequestInfo requestInfo) {
		List<Document> ownerDocs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
			property.getPropertyDetails().getOwners().forEach(owner -> {
				List<Document> ownerDocuments = owner.getOwnerDetails().getOwnerDocuments();
				if (!CollectionUtils.isEmpty(ownerDocuments)) {
					ownerDocuments.forEach(document -> {
						if (document.getId() == null || document.getId().isEmpty()) {
							AuditDetails docAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
									true);
							String gen_doc_id = UUID.randomUUID().toString();
							document.setId(gen_doc_id);
							document.setTenantId(property.getTenantId());
							document.setReferenceId(owner.getOwnerDetails().getId());
							document.setPropertyId(property.getId());
							document.setAuditDetails(docAuditDetails);
						} else {
							AuditDetails docAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
									true);
							document.setAuditDetails(docAuditDetails);
						}
					});
					ownerDocs.addAll(ownerDocuments);
				}
			});
		}
		return ownerDocs;
	}

	private List<CourtCase> getCourtCases(Property property, RequestInfo requestInfo) {

		List<CourtCase> courtCases = property.getPropertyDetails().getCourtCases();

		if (!CollectionUtils.isEmpty(courtCases)) {

			courtCases.forEach(courtCase -> {
				if (courtCase.getId() == null || courtCase.getId().isEmpty()) {

					AuditDetails courtCaseAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
							true);
					String gen_court_case_id = UUID.randomUUID().toString();

					courtCase.setId(gen_court_case_id);
					courtCase.setTenantId(property.getTenantId());
					courtCase.setPropertyDetailsId(property.getPropertyDetails().getId());
					courtCase.setAuditDetails(courtCaseAuditDetails);
				} else {
					AuditDetails courtCaseAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
							true);
					courtCase.setAuditDetails(courtCaseAuditDetails);
				}
			});
		}
		return courtCases;
	}

	private List<Payment> getPaymentDetails(Property property, RequestInfo requestInfo) {
		List<Payment> paymentDetails = new ArrayList<>();
		if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
			property.getPropertyDetails().getOwners().forEach(owner -> {
				List<Payment> payments = property.getPropertyDetails().getPaymentDetails();
				if (!CollectionUtils.isEmpty(payments)) {
					payments.forEach(payment -> {
						if (payment.getId() == null || payment.getId().isEmpty()) {
							AuditDetails paymentAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
									true);
							String gen_payment_detail_id = UUID.randomUUID().toString();
							payment.setId(gen_payment_detail_id);
							payment.setTenantId(property.getTenantId());
							payment.setOwnerDetailsId(owner.getOwnerDetails().getId());
							payment.setAuditDetails(paymentAuditDetails);
						} else {
							AuditDetails paymentAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),
									true);
							payment.setAuditDetails(paymentAuditDetails);
						}
					});
					paymentDetails.addAll(payments);
				}
			});
		}
		return paymentDetails;
	}

	/**
	 * Application Related Enrich
	 */

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
				enrichGenerateDemand(application);
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

	private void enrichGenerateDemand(Application application) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();

		if (application.getState().contains(PSConstants.EM_STATE_PENDING_DA_FEE)) {

			TaxHeadEstimate estimateDue = new TaxHeadEstimate();
			estimateDue.setEstimateAmount(new BigDecimal(500.00));
			estimateDue.setCategory(Category.FEE);
			estimateDue.setTaxHeadCode(getTaxHeadCodeWithCharge(application.getBillingBusinessService(),
					PSConstants.TAX_HEAD_CODE_APPLICATION_CHARGE, Category.FEE));
			estimates.add(estimateDue);
		}
		Calculation calculation = Calculation.builder().applicationNumber(application.getApplicationNumber())
				.taxHeadEstimates(estimates).tenantId(application.getTenantId()).build();
		application.setCalculation(calculation);
	}

//	To be used in future
	private void enrichUpdateDemand(Application application) {
		List<TaxHeadEstimate> estimates = new LinkedList<>();

		if (application.getAction().equalsIgnoreCase(PSConstants.EM_ACTION_APPROVE)) {

			TaxHeadEstimate estimateDue = new TaxHeadEstimate();
			estimateDue.setEstimateAmount(new BigDecimal(500.00));
			estimateDue.setCategory(Category.FEE);
			estimateDue.setTaxHeadCode(getTaxHeadCodeWithCharge(application.getBillingBusinessService(),
					PSConstants.TAX_HEAD_CODE_APPLICATION_CHARGE, Category.FEE));
			estimates.add(estimateDue);
		}
		Calculation calculation = Calculation.builder().applicationNumber(application.getApplicationNumber())
				.taxHeadEstimates(estimates).tenantId(application.getTenantId()).build();
		application.setCalculation(calculation);
	}

	private String getTaxHeadCodeWithCharge(String billingBusService, String chargeFor, Category category) {
		return String.format("%s_%s_%s", billingBusService, chargeFor, category.toString());
	}
	
	public AuctionSaveRequest enrichAuctionCreateRequest(ExcelSearchCriteria searchCriteria,
			AuctionTransactionRequest auctionTransactionRequest) {
		Property property = auctionTransactionRequest.getProperty();
		List<Auction> auctions = new ArrayList<>();
		AuctionSaveRequest request = AuctionSaveRequest.builder()
				.requestInfo(auctionTransactionRequest.getRequestInfo()).build();
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auctionAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		try {
			String filePath = fileStoreUtils.fetchFileStoreUrl(searchCriteria);
			if (!filePath.isEmpty()) {				
				auctions = readExcelService.getDatafromExcel(new UrlResource(filePath).getInputStream(), 0);
				auctions.forEach(auction -> {
					String gen_auction_id = UUID.randomUUID().toString();
					auction.setAuditDetails(auctionAuditDetails);
					auction.setAuctionId(auction.getId());
					auction.setId(gen_auction_id);
					auction.setPropertyId(property.getId());
					auction.setTenantId(property.getTenantId());
					auction.setFileNumber(property.getFileNumber());
				});
			}
			request.setAuctions(auctions);
		} catch (Exception e) {
			log.error("Error occur during runnig controller method readExcel():" + e.getMessage());
		}
		return request;
	}
	
	public void enrichUpdateAuctionRequest(AuctionSaveRequest request, List<Auction> auctionFromSearch) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), false);

		if (!CollectionUtils.isEmpty(request.getAuctions())) {
			request.getAuctions().forEach(auction -> {
				auction.getAuditDetails().setLastModifiedBy(auditDetails.getLastModifiedBy());
				auction.getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());
			});
		}
	}
}
