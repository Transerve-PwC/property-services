package org.egov.ps.workflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.model.Property;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Service
@Slf4j
public class WorkflowIntegrator {

	private static final String TENANTIDKEY = "tenantId";

	private static final String BUSINESSSERVICEKEY = "businessService";

	private static final String BUSINESSIDKEY = "businessId";

	private static final String ACTIONKEY = "action";

	private static final String MODULENAMEKEY = "moduleName";

	private static final String STATEKEY = "state";

	private static final String COMMENTKEY = "comment";

	private static final String DOCUMENTSKEY = "documents";

	private static final String ASSIGNERKEY = "assigner";

	private static final String ASSIGNEEKEY = "assignee";

	private static final String UUIDKEY = "uuid";

	private static final String MODULENAMEVALUE = "EP";

	private static final String BPAMODULENAMEVALUE = "BPAREG";

	private static final String WORKFLOWREQUESTARRAYKEY = "ProcessInstances";

	private static final String REQUESTINFOKEY = "RequestInfo";

	private static final String PROCESSINSTANCESJOSNKEY = "$.ProcessInstances";

	private static final String BUSINESSIDJOSNKEY = "$.businessId";

	private static final String STATUSJSONKEY = "$.state.applicationStatus";

	private static final String AUDITDETAILSKEY = "auditDetails";

	private RestTemplate rest;

	private Configuration config;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromTradeTypeOnly;

	@Autowired
	public WorkflowIntegrator(RestTemplate rest, Configuration config) {
		this.rest = rest;
		this.config = config;
	}

	/**
	 * Method to integrate with workflow
	 *
	 * takes the property request as parameter constructs the work-flow request
	 *
	 * and sets the resultant status from wf-response back to property object
	 *
	 * @param request
	 */
	public void callWorkFlow(PropertyRequest request) {

		String wfTenantId = request.getProperties().get(0).getTenantId();
		JSONArray array = new JSONArray();
		for (Property property : request.getProperties()) {
			JSONObject obj = new JSONObject();
			List<Map<String, String>> uuidmaps = new LinkedList<>();
			List<Map<String, String>> assigneeUuidmaps = new LinkedList<>();

//			if (!CollectionUtils.isEmpty(property.getAssignee())) {
//
//				// Adding assignees to processInstance
//				property.getAssignee().forEach(assignee -> {
//					Map<String, String> uuidMap = new HashMap<>();
//					uuidMap.put(UUIDKEY, assignee);
//					assigneeUuidmaps.add(uuidMap);
//				});
//			}

			if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
				property.getPropertyDetails().getOwners().forEach(owners -> {
					Map<String, String> uuidMap = new HashMap<>();
					uuidMap.put(UUIDKEY, owners.getId());
					uuidmaps.add(uuidMap);
				});
			}
			obj.put(TENANTIDKEY, wfTenantId);
			obj.put(BUSINESSSERVICEKEY, config.getPsBusinessServiceValue());
			obj.put(BUSINESSIDKEY, property.getFileNumber());
			obj.put(ACTIONKEY, property.getAction());
			obj.put(MODULENAMEKEY, MODULENAMEVALUE);
			obj.put(AUDITDETAILSKEY, property.getAuditDetails());
//			obj.put(COMMENTKEY, property.getComment());
//			if (!CollectionUtils.isEmpty(property.getAssignee())) {
//				if (uuidmaps.size() == 1) {
//					obj.put(ASSIGNEEKEY, assigneeUuidmaps.get(0));
//				} else {
//					obj.put(ASSIGNEEKEY, assigneeUuidmaps);
//				}
//			}

			array.add(obj);
		}
		Map<String, String> idStatusMap = callCommonWorkflow(array, request.getRequestInfo());

		// setting the status back to Property object from wf response
		request.getProperties().forEach(property -> {
			property.setState(idStatusMap.get(property.getFileNumber()));
		});
	}

	public void callApplicationWorkFlow(ApplicationRequest request) {

		String wfTenantId = request.getApplications().get(0).getTenantId();
		JSONArray array = new JSONArray();
		for (Application application : request.getApplications()) {
			JSONObject obj = new JSONObject();
			List<Map<String, String>> uuidmaps = new LinkedList<>();
			List<Map<String, String>> assigneeUuidmaps = new LinkedList<>();

			obj.put(TENANTIDKEY, wfTenantId);
			obj.put(BUSINESSSERVICEKEY, application.getBusinessService());
			obj.put(BUSINESSIDKEY, application.getApplicationNumber());
			obj.put(ACTIONKEY, application.getAction());
			obj.put(MODULENAMEKEY, MODULENAMEVALUE);
			obj.put(AUDITDETAILSKEY, application.getAuditDetails());

			array.add(obj);
		}
		Map<String, String> idStatusMap = callCommonWorkflow(array, request.getRequestInfo());

		// setting the status back to Property object from wf response
		request.getApplications().forEach(application -> {
			application.setState(idStatusMap.get(application.getApplicationNumber()));
		});
	}

	private Map<String, String> callCommonWorkflow(JSONArray array, RequestInfo requestInfo) {

		Map<String, String> idStatusMap = new HashMap<>();
		if (!array.isEmpty()) {
			JSONObject workFlowRequest = new JSONObject();
			workFlowRequest.put(REQUESTINFOKEY, requestInfo);
			workFlowRequest.put(WORKFLOWREQUESTARRAYKEY, array);
			String response = null;
			try {
				response = rest.postForObject(config.getWfHost().concat(config.getWfTransitionPath()), workFlowRequest,
						String.class);
			} catch (HttpClientErrorException e) {

				/*
				 * extracting message from client error exception
				 */
				DocumentContext responseContext = JsonPath.parse(e.getResponseBodyAsString());
				List<Object> errros = null;
				try {
					errros = responseContext.read("$.Errors");
				} catch (PathNotFoundException pnfe) {
					log.error("EG_CSP_WF_ERROR_KEY_NOT_FOUND",
							" Unable to read the json path in error object : " + pnfe.getMessage());
					throw new CustomException("EG_CSP_WF_ERROR_KEY_NOT_FOUND",
							" Unable to read the json path in error object : " + pnfe.getMessage());
				}
				throw new CustomException("EG_WF_ERROR", errros.toString());
			} catch (Exception e) {
				throw new CustomException("EG_WF_ERROR",
						" Exception occured while integrating with workflow : " + e.getMessage());
			}

			/*
			 * on success result from work-flow read the data and set the status back to
			 * Property object
			 */
			DocumentContext responseContext = JsonPath.parse(response);
			List<Map<String, Object>> responseArray = responseContext.read(PROCESSINSTANCESJOSNKEY);
			responseArray.forEach(object -> {
				DocumentContext instanceContext = JsonPath.parse(object);
				idStatusMap.put(instanceContext.read(BUSINESSIDJOSNKEY), instanceContext.read(STATUSJSONKEY));
			});
		}
		return idStatusMap;
	}

}
