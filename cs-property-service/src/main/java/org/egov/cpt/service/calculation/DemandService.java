package org.egov.cpt.service.calculation;

import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.calculation.Demand;
import org.egov.cpt.models.calculation.Demand.StatusEnum;
import org.egov.cpt.models.calculation.DemandDetail;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.OwnershipTransferRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DemandService {

	@Autowired
	private PropertyUtil utils;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRepository demandRepository;

	public void addCalculation(OwnershipTransferRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		List<Owner> owners = request.getOwners();
		Object mdmsData = null;

		createDemand(requestInfo, owners, mdmsData);
	}

	/**
	 * Creates demand for the given list of calculations
	 * 
	 * @param requestInfo The RequestInfo of the calculation request
	 * @param owners      List of calculation object
	 * @return Demands that are created
	 */
	private List<Demand> createDemand(RequestInfo requestInfo, List<Owner> owners, Object mdmsData) {
		List<Demand> demands = new LinkedList<>();
		for (Owner owner : owners) {
			if (owner == null)
				throw new CustomException("INVALID APPLICATIONNUMBER",
						"Demand cannot be generated for applicationNumber "
								+ owner.getOwnerDetails().getApplicationNumber()
								+ " TradeLicense with this number does not exist ");

			String tenantId = owner.getTenantId();
			String consumerCode = owner.getOwnerDetails().getApplicationNumber();

			User user = User.builder().id(Long.parseLong(owner.getId())).userName(owner.getOwnerDetails().getName())
					.name(owner.getOwnerDetails().getName()).mobileNumber(owner.getOwnerDetails().getPhone())
					.emailId(owner.getOwnerDetails().getEmail()).tenantId(owner.getOwnerDetails().getTenantId())
					.build();

			List<DemandDetail> demandDetails = new LinkedList<>();

//			calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
//				demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
//						.taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode()).collectionAmount(BigDecimal.ZERO)
//						.tenantId(tenantId).build());
//			});
			Long taxPeriodFrom = System.currentTimeMillis();
			Long taxPeriodTo = System.currentTimeMillis();
			String businessService = owner.getBusinessService();

//			.status(StatusEnum.ACTIVE)
//            .consumerCode(consumerCode)
//            .demandDetails(demandDetails)
//            .payer(owner)
//            .minimumAmountPayable(config.getMinimumPayableAmount())
//            .tenantId(tenantId)
//            .taxPeriodFrom(taxPeriodFrom)
//            .taxPeriodTo(taxPeriodTo)
//            .consumerType("tradelicense")
//            .businessService(businessService)
//            .additionalDetails(Collections.singletonMap(BILLINGSLAB_KEY, combinedBillingSlabs))
			Demand singleDemand = Demand.builder().status(StatusEnum.ACTIVE).consumerCode(consumerCode)
					.demandDetails(demandDetails).payer(user).minimumAmountPayable(config.getMinimumPayableAmount())
					.tenantId(tenantId).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
					.consumerType("rentedproperties") // TODO
					.businessService(businessService) // TODO
					.additionalDetails("").build();

			demands.add(singleDemand);
		}
		return demandRepository.saveDemand(requestInfo, demands);
	}

}
