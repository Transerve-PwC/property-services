package org.egov.cpt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Owner;
import org.egov.cpt.models.calculation.BusinessService;
import org.egov.cpt.workflow.WorkflowService;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtil {

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private WorkflowService workflowService;

	/**
	 * Method to return auditDetails for create/update flows
	 *
	 * @param by
	 * @param isCreate
	 * @return AuditDetails
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate) {

		Long time = System.currentTimeMillis();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {

		List<MasterDetail> masterDetails = new ArrayList<>();

		names.forEach(name -> {
			masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
		});

		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/**
	 * Creates url for tl-calculator service
	 * 
	 * @return url for tl-calculator service
	 */
//	public StringBuilder getCalculationURI(String businessService) {
//		StringBuilder uri = new StringBuilder();
//		uri.append(config.getCalculatorHost());
//		uri.append(config.getCalculateEndpointTL());
//		return uri;
//	}

	/**
	 * Creates demand Search url based on tenanatId,businessService and ConsumerCode
	 * 
	 * @return demand search url
	 */
	public String getDemandSearchURL() {
		StringBuilder url = new StringBuilder(config.getBillingHost());
		url.append(config.getDemandSearchEndpoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("businessService=");
		url.append("{2}");
		url.append("&");
		url.append("consumerCode=");
		url.append("{3}");
		return url.toString();
	}

	/**
	 * Creates a map of id to isStateUpdatable
	 * 
	 * @param searchresult    Licenses from DB
	 * @param businessService The businessService configuration
	 * @return Map of is to isStateUpdatable
	 */
	public Map<String, Boolean> getIdToIsStateUpdatableMap(BusinessService businessService, List<Owner> searchresult) {
		Map<String, Boolean> idToIsStateUpdatableMap = new HashMap<>();
		searchresult.forEach(result -> {
			String nameofBusinessService = result.getBusinessService();
			if (StringUtils.equals(nameofBusinessService, PTConstants.BUSINESS_SERVICE_OT)
					&& (result.getApplicationState().equalsIgnoreCase(PTConstants.STATUS_INITIATED))) {
				idToIsStateUpdatableMap.put(result.getId(), true);
			} else
				idToIsStateUpdatableMap.put(result.getId(),
						workflowService.isStateUpdatable(result.getApplicationState(), businessService));
		});
		return idToIsStateUpdatableMap;
	}

	public Map<String, Boolean> getIdToIsStateUpdatableMapDc(BusinessService businessService,
			List<DuplicateCopy> searchresult) {
		Map<String, Boolean> idToIsStateUpdatableMapDc = new HashMap<>();
		searchresult.forEach(result -> {
			String nameofBusinessService = result.getBusinessService();
			if (StringUtils.equals(nameofBusinessService, PTConstants.BUSINESS_SERVICE_DC)
					&& (result.getState().equalsIgnoreCase(PTConstants.STATUS_INITIATED))) {
				idToIsStateUpdatableMapDc.put(result.getId(), true);
			} else
				idToIsStateUpdatableMapDc.put(result.getId(),
						workflowService.isStateUpdatable(result.getState(), businessService));
		});
		return idToIsStateUpdatableMapDc;
	}
}
