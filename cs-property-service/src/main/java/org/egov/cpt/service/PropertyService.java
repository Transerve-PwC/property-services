package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.AccountStatementCriteria;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.models.calculation.State;
import org.egov.cpt.producer.Producer;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.validator.PropertyValidator;
import org.egov.cpt.web.contracts.AccountStatementResponse;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.egov.cpt.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyService {

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private UserService userService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private RentEnrichmentService rentEnrichmentService;

	@Autowired
	private IRentCollectionService rentCollectionService;

	public List<Property> createProperty(PropertyRequest request) {

		propertyValidator.validateCreateRequest(request);
		enrichmentService.enrichCreateRequest(request);
		processRentHistory(request);
		userService.createUser(request);

		producer.push(config.getSavePropertyTopic(), request);

		processRentSummary(request);
		return request.getProperties();
	}

	private void processRentSummary(PropertyRequest request) {
		request.getProperties().stream().filter(property -> property.getDemands() != null
				&& property.getPayments() != null && property.getRentAccount() != null).forEach(property -> {
					property.setRentSummary(rentCollectionService.calculateRentSummary(property.getDemands(),
							property.getRentAccount(), property.getPropertyDetails().getInterestRate()));
				});
	}

	/**
	 * Updates the property
	 *
	 * @param request PropertyRequest containing list of properties to be update
	 * @return List of updated properties
	 */
	public List<Property> updateProperty(PropertyRequest request) {
		List<Property> propertyFromSearch = propertyValidator.validateUpdateRequest(request);
		enrichmentService.enrichUpdateRequest(request, propertyFromSearch);
		processRentHistory(request);
		userService.createUser(request);
		String action =request.getProperties().get(0).getMasterDataAction();
		String state =request.getProperties().get(0).getMasterDataState();
		if ((config.getIsWorkflowEnabled()&& !action.equalsIgnoreCase(""))
				&& (!state.equalsIgnoreCase(PTConstants.PM_STATUS_APPROVED)) ) {
			wfIntegrator.callWorkFlow(request);
		}
		producer.push(config.getUpdatePropertyTopic(), request);

		processRentSummary(request);
		return request.getProperties();
	}

	private void processRentHistory(PropertyRequest request) {
		rentEnrichmentService.enrichRentdata(request);
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().stream().filter(property -> property.getDemands() != null
					&& property.getPayments() != null && property.getRentAccount() != null).forEach(property -> {
						property.setRentCollections(
								rentCollectionService.settle(property.getDemands(), property.getPayments(),
										property.getRentAccount(), property.getPropertyDetails().getInterestRate()));
					});
		}
		rentEnrichmentService.enrichCollection(request);
	}

	public AccountStatementResponse searchPayments(AccountStatementCriteria accountStatementCriteria,
			RequestInfo requestInfo) {

		List<Property> properties = repository
				.getProperties(PropertyCriteria.builder().propertyId(accountStatementCriteria.getPropertyid())
						.relations(Collections.singletonList("finance")).build());
		if (CollectionUtils.isEmpty(properties)) {
			return AccountStatementResponse.builder().rentAccountStatements(Collections.emptyList()).build();			
		}
		
		Property property = properties.get(0);
		List<RentDemand> demands = repository
				.getPropertyRentDemandDetails(PropertyCriteria.builder().propertyId(property.getId()).build())
				.stream()
				.filter(rentDemand -> accountStatementCriteria.getFromDate() <= rentDemand.getAuditDetails().getCreatedTime())
				.filter(rentDemand -> accountStatementCriteria.getToDate() >= rentDemand.getAuditDetails().getCreatedTime())
				.map(rentDemand-> {	return rentDemand; })
				.collect(Collectors.toList());
		
		List<RentPayment> payments = repository
				.getPropertyRentPaymentDetails(PropertyCriteria.builder().propertyId(property.getId()).build())
				.stream()
				.filter(rentPayment -> accountStatementCriteria.getFromDate() <= rentPayment.getAuditDetails().getCreatedTime())
				.filter(rentPayment -> accountStatementCriteria.getToDate() >= rentPayment.getAuditDetails().getCreatedTime())
				.map(rentPayment-> {	return rentPayment; })
				.collect(Collectors.toList());
		
		
		return AccountStatementResponse.builder()
				.rentAccountStatements(rentCollectionService.getAccountStatement(demands, payments,
						property.getPropertyDetails().getInterestRate(), accountStatementCriteria.getFromDate(),
						accountStatementCriteria.getToDate()))
				.build();
	}
	

	public List<Property> searchProperty(PropertyCriteria criteria, RequestInfo requestInfo) {
		if (criteria.isEmpty()) {
			String wfbusinessServiceName = PTConstants.BUSINESS_SERVICE_PM;
			BusinessService otBusinessService = workflowService.getBusinessService(criteria.getTenantId(), requestInfo,
					wfbusinessServiceName);
			List<State> stateList = otBusinessService.getStates();
			List<String> states = new ArrayList<String>();

			for (State state : stateList) {
				states.add(state.getState());
			}
			states.remove("");
			states.remove(PTConstants.PM_DRAFTED);
			log.info("states:" + states);
			criteria.setState(states);
			criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		} else {
			if (!CollectionUtils.isEmpty(criteria.getState()) && criteria.getState().contains(PTConstants.PM_DRAFTED))
				criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		}
		List<Property> properties = repository.getProperties(criteria);
		if (CollectionUtils.isEmpty(properties))
			return Collections.emptyList();

		if (properties.size() <= 1 || !CollectionUtils.isEmpty(criteria.getRelations())
				&& criteria.getRelations().contains(PTConstants.RELATION_FINANCE)) {
			properties.stream().forEach(property -> {
				List<RentDemand> demands = repository
						.getPropertyRentDemandDetails(PropertyCriteria.builder().propertyId(property.getId()).build());
				List<RentPayment> payments = repository
						.getPropertyRentPaymentDetails(PropertyCriteria.builder().propertyId(property.getId()).build());
				RentAccount accounts = repository
						.getPropertyRentAccountDetails(PropertyCriteria.builder().propertyId(property.getId()).build());
				if (!CollectionUtils.isEmpty(demands) && null != accounts) {
					property.setRentSummary(rentCollectionService.calculateRentSummary(demands, accounts,
							property.getPropertyDetails().getInterestRate()));
					property.setDemands(demands);
					property.setPayments(payments);
				}
			});
		}

		return properties;
	}
}
