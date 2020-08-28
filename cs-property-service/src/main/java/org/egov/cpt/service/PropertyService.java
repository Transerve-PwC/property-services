package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.AccountStatementCriteria;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
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
					property.setRentSummary(
							rentCollectionService.paymentSummary(property.getDemands(), property.getRentAccount()));
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
		if (config.getIsWorkflowEnabled()
				&& !request.getProperties().get(0).getMasterDataAction().equalsIgnoreCase("")) {
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
	
	public AccountStatementResponse searchPayments(AccountStatementCriteria accountStatementCriteria,RequestInfo requestInfo) {		
		List<RentPayment> payments = repository.getRentPayments(accountStatementCriteria);
		accountStatementCriteria.setPaymentids(payments.stream().map(RentPayment::getId).collect(Collectors.toList()));
		
		List<RentDemand> demands = repository.getRentDemands(accountStatementCriteria);
		accountStatementCriteria.setDemandids(demands.stream().map(RentDemand::getId).collect(Collectors.toList()));
		
		List<RentCollection> collections = repository.getRentCollections(accountStatementCriteria);		
		return AccountStatementResponse.builder().rentAccountStatements(rentCollectionService.accountStatement(demands, payments, collections)).build();
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
					property.setRentSummary(rentCollectionService.paymentSummary(demands, accounts));
					property.setDemands(demands);
					property.setPayments(payments);
				}
			});
		}

		return properties;
	}
}
