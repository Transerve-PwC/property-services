package org.egov.cpt.service.calculation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.RequestInfoWrapper;
import org.egov.cpt.models.calculation.Demand;
import org.egov.cpt.models.calculation.Demand.StatusEnum;
import org.egov.cpt.models.calculation.DemandDetail;
import org.egov.cpt.models.calculation.DemandResponse;
import org.egov.cpt.models.calculation.TaxHeadEstimate;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.PropertyUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DemandService {

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PropertyUtil utils;

	/**
	 * Creates demand for the given list of calculations
	 * 
	 * @param requestInfo The RequestInfo of the calculation request
	 * @param owners      List of calculation object
	 * @return Demands that are created
	 */
	public List<Demand> createDemand(RequestInfo requestInfo, List<Owner> owners) {
		List<Demand> demands = new LinkedList<>();
		for (Owner owner : owners) {
			if (owner == null)
				throw new CustomException("INVALID APPLICATIONNUMBER",
						"Demand cannot be generated for this application");

			String tenantId = owner.getTenantId();
			String consumerCode = owner.getOwnerDetails().getApplicationNumber();

			User requestUser = requestInfo.getUserInfo(); // user from request information
			User user = User.builder().id(requestUser.getId()).userName(requestUser.getUserName())
					.name(requestUser.getName()).type(requestInfo.getUserInfo().getType())
					.mobileNumber(requestUser.getMobileNumber()).emailId(requestUser.getEmailId())
					.roles(requestUser.getRoles()).tenantId(requestUser.getTenantId()).uuid(requestUser.getUuid())
					.build();

			List<DemandDetail> demandDetails = new LinkedList<>();
			if (!CollectionUtils.isEmpty(owner.getCalculation().getTaxHeadEstimates())) {
				owner.getCalculation().getTaxHeadEstimates().forEach(taxHeadEstimate -> {
					demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
							.tenantId(tenantId).build());
				});
			}

			Long taxPeriodFrom = System.currentTimeMillis();
			Long taxPeriodTo = System.currentTimeMillis();
			String businessService = owner.getBusinessService();

			Demand singleDemand = Demand.builder().status(StatusEnum.ACTIVE).consumerCode(consumerCode)
					.demandDetails(demandDetails).payer(user).minimumAmountPayable(config.getMinimumPayableAmount())
					.tenantId(tenantId).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
					.consumerType("rentedproperties").businessService(businessService).additionalDetails(null).build();

			demands.add(singleDemand);
		}
		return demandRepository.saveDemand(requestInfo, demands);
	}

	/**
	 * Updates demand for the given list of calculations
	 * 
	 * @param requestInfo  The RequestInfo of the calculation request
	 * @param calculations List of calculation object
	 * @return Demands that are updated
	 */
	public List<Demand> updateDemand(RequestInfo requestInfo, List<Owner> owners) {
		List<Demand> demands = new LinkedList<>();
		for (Owner owner : owners) {

			List<Demand> searchResult = searchDemand(owner.getTenantId(),
					Collections.singleton(owner.getOwnerDetails().getApplicationNumber()), requestInfo,
					owner.getBusinessService());

			if (CollectionUtils.isEmpty(searchResult))
				throw new CustomException("INVALID UPDATE",
						"No demand exists for applicationNumber: " + owner.getOwnerDetails().getApplicationNumber());

			Demand demand = searchResult.get(0);
			List<DemandDetail> demandDetails = demand.getDemandDetails();
			List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(owner, demandDetails);
			demand.setDemandDetails(updatedDemandDetails);
			demands.add(demand);
		}
		return demandRepository.updateDemand(requestInfo, demands);
	}

	/**
	 * Searches demand for the given consumerCode and tenantIDd
	 * 
	 * @param tenantId      The tenantId of the tradeLicense
	 * @param consumerCodes The set of consumerCode of the demands
	 * @param requestInfo   The RequestInfo of the incoming request
	 * @return Lis to demands for the given consumerCode
	 */
	private List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo,
			String businessService) {
		String uri = utils.getDemandSearchURL();
		uri = uri.replace("{1}", tenantId);
		uri = uri.replace("{2}", businessService);
		uri = uri.replace("{3}", StringUtils.join(consumerCodes, ','));

		Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());

		DemandResponse response;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response from Demand Search");
		}

		if (CollectionUtils.isEmpty(response.getDemands())) {
			return null;
		} else {
			return response.getDemands();
		}
	}

	/**
	 * Returns the list of new DemandDetail to be added for updating the demand
	 * 
	 * @param owner         The calculation object for the update tequest
	 * @param demandDetails The list of demandDetails from the existing demand
	 * @return The list of new DemandDetails
	 */
	private List<DemandDetail> getUpdatedDemandDetails(Owner owner, List<DemandDetail> demandDetails) {

		List<DemandDetail> newDemandDetails = new ArrayList<>();
		Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

		demandDetails.forEach(demandDetail -> {
			if (!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())) {
				List<DemandDetail> demandDetailList = new LinkedList<>();
				demandDetailList.add(demandDetail);
				taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(), demandDetailList);
			} else
				taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
		});

		BigDecimal diffInTaxAmount;
		List<DemandDetail> demandDetailList;
		BigDecimal total;

		for (TaxHeadEstimate taxHeadEstimate : owner.getCalculation().getTaxHeadEstimates()) {
			if (!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
				newDemandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(owner.getTenantId())
						.collectionAmount(BigDecimal.ZERO).build());
			else {
				demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
				total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
				if (diffInTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(diffInTaxAmount)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(owner.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
				}
			}
		}
		List<DemandDetail> combinedBillDetials = new LinkedList<>(demandDetails);
		combinedBillDetials.addAll(newDemandDetails);
		return combinedBillDetials;
	}

	public List<Demand> createDuplicateCopyDemand(RequestInfo requestInfo,
			List<DuplicateCopy> duplicateCopyApplications) {
		List<Demand> demands = new LinkedList<>();
		for (DuplicateCopy application : duplicateCopyApplications) {
			if (application == null) {
				throw new CustomException("INVALID APPLICATIONNUMBER",
						"Demand cannot be generated for this application");
			}

			String tenantId = application.getTenantId();
			String consumerCode = application.getApplicationNumber();

			User requestUser = requestInfo.getUserInfo();
			User user = User.builder().id(requestUser.getId()).userName(requestUser.getUserName())
					.name(requestUser.getName()).type(requestInfo.getUserInfo().getType())
					.mobileNumber(requestUser.getMobileNumber()).emailId(requestUser.getEmailId())
					.roles(requestUser.getRoles()).tenantId(requestUser.getTenantId()).uuid(requestUser.getUuid())
					.build();

			List<DemandDetail> demandDetails = new LinkedList<>();
			if (!CollectionUtils.isEmpty(application.getCalculation().getTaxHeadEstimates())) {
				application.getCalculation().getTaxHeadEstimates().forEach(taxHeadEstimate -> {
					demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
							.tenantId(tenantId).build());
				});
			}

			Long taxPeriodFrom = System.currentTimeMillis();
			Long taxPeriodTo = System.currentTimeMillis();
			String businessService = application.getBusinessService();

			Demand singleDemand = Demand.builder().status(StatusEnum.ACTIVE).consumerCode(consumerCode)
					.demandDetails(demandDetails).payer(user).minimumAmountPayable(config.getMinimumPayableAmount())
					.tenantId(tenantId).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
					.consumerType("rentedproperties").businessService(businessService).additionalDetails(null).build();

			demands.add(singleDemand);
		}

		return demandRepository.saveDemand(requestInfo, demands);

	}

	public List<Demand> updateDuplicateCopyDemand(RequestInfo requestInfo,
			List<DuplicateCopy> duplicateCopyApplications) {
		List<Demand> demands = new LinkedList<>();
		for (DuplicateCopy application : duplicateCopyApplications) {

			List<Demand> searchResult = searchDemand(application.getTenantId(),
					Collections.singleton(application.getApplicationNumber()), requestInfo,
					application.getBusinessService());
			if (CollectionUtils.isEmpty(searchResult)) {
				throw new CustomException("INVALID UPDATE",
						"No demand exists for applicationNumber: " + application.getApplicationNumber());

			}
			Demand demand = searchResult.get(0);
			List<DemandDetail> demandDetails = demand.getDemandDetails();
			List<DemandDetail> updatedDemandDetails = getUpdatedDuplicateCopyDemandDetails(application, demandDetails);
			demand.setDemandDetails(updatedDemandDetails);
			demands.add(demand);
		}
		return demands;

	}

	private List<DemandDetail> getUpdatedDuplicateCopyDemandDetails(DuplicateCopy application,
			List<DemandDetail> demandDetails) {
		 
		List<DemandDetail> newDemandDetails = new ArrayList<>();
		Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

		demandDetails.forEach(demandDetail -> {
			if (!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())) {
				List<DemandDetail> demandDetailList = new LinkedList<>();
				demandDetailList.add(demandDetail);
				taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(), demandDetailList);
			} else
				taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
		});

		BigDecimal diffInTaxAmount;
		List<DemandDetail> demandDetailList;
		BigDecimal total;

		for (TaxHeadEstimate taxHeadEstimate : application.getCalculation().getTaxHeadEstimates()) {
			if (!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
				newDemandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(application.getTenantId())
						.collectionAmount(BigDecimal.ZERO).build());
			else {
				demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
				total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO,
						BigDecimal::add);
				diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
				if (diffInTaxAmount.compareTo(BigDecimal.ZERO) != 0) {
					newDemandDetails.add(DemandDetail.builder().taxAmount(diffInTaxAmount)
							.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).tenantId(application.getTenantId())
							.collectionAmount(BigDecimal.ZERO).build());
				}
			}
		}
		List<DemandDetail> combinedBillDetials = new LinkedList<>(demandDetails);
		combinedBillDetials.addAll(newDemandDetails);
		return combinedBillDetials;
	}

}