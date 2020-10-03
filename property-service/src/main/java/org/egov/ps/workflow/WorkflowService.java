package org.egov.ps.workflow;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.repository.ServiceRequestRepository;
import org.egov.ps.web.contracts.BusinessService;
import org.egov.ps.web.contracts.BusinessServiceRequest;
import org.egov.ps.web.contracts.BusinessServiceResponse;
import org.egov.ps.web.contracts.RequestInfoWrapper;
import org.egov.ps.web.contracts.State;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowService {

	private Configuration config;

	private ServiceRequestRepository serviceRequestRepository;

	private ObjectMapper mapper;

	@Autowired
	public WorkflowService(Configuration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	public BusinessServiceResponse createBusinessService(RequestInfo requestInfo,
			List<BusinessService> businessServices) {
		String url = config.getWfHost() + config.getWorkflowBusinessServiceCreatePath();
		BusinessServiceRequest requestObj = BusinessServiceRequest.builder().businessServices(businessServices)
				.requestInfo(requestInfo).build();
		BusinessServiceResponse response = this.serviceRequestRepository.fetchResult(url, requestObj,
				BusinessServiceResponse.class);
		return response;
	}

	/**
	 * Get the workflow config for the given tenant
	 * 
	 * @param tenantId    The tenantId for which businessService is requested
	 * @param requestInfo The RequestInfo object of the request
	 * @return BusinessService for the the given tenantId
	 */
	public BusinessService getBusinessService(String tenantId, RequestInfo requestInfo, String businessServiceName) {

		StringBuilder url = getSearchURLWithParams(tenantId, businessServiceName);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		BusinessServiceResponse response = null;
		try {
			response = mapper.convertValue(result, BusinessServiceResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of getBusinessService");
		}
		if (CollectionUtils.isEmpty(response.getBusinessServices())) {
			throw new CustomException("NO BUSINESS SERVICE FOUND",
					"Could not find business service '" + businessServiceName + "'");
		}
		return response.getBusinessServices().get(0);
	}

	/**
	 * Creates url for search based on given tenantId
	 *
	 * @param tenantId The tenantId for which url is generated
	 * @return The search url
	 */
	private StringBuilder getSearchURLWithParams(String tenantId, String businessServiceName) {
		StringBuilder url = new StringBuilder(config.getWfHost());
		url.append(config.getWfBusinessServiceSearchPath());
		url.append("?tenantId=");
		url.append(tenantId);
		url.append("&businessServices=");
		url.append(businessServiceName);
		return url;
	}

	/**
	 * Returns boolean value to specifying if the state is updatable
	 * 
	 * @param stateCode       The stateCode of the license
	 * @param businessService The BusinessService of the application flow
	 * @return State object to be fetched
	 */
	public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
		for (State state : businessService.getStates()) {
			if (state.getApplicationStatus() != null && state.getApplicationStatus().equalsIgnoreCase(stateCode))
				return state.getIsStateUpdatable();
		}
		return null;
	}

}
