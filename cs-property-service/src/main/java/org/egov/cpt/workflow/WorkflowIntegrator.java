package org.egov.cpt.workflow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.DuplicateCopy;
import org.egov.cpt.models.Property;
import org.egov.cpt.web.contracts.DuplicateCopyRequest;
import org.egov.cpt.web.contracts.PropertyRequest;
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

	private static final String MODULENAMEVALUE = "csp";

	private static final String BPAMODULENAMEVALUE = "BPAREG";

	private static final String WORKFLOWREQUESTARRAYKEY = "ProcessInstances";

	private static final String REQUESTINFOKEY = "RequestInfo";

	private static final String PROCESSINSTANCESJOSNKEY = "$.ProcessInstances";

	private static final String BUSINESSIDJOSNKEY = "$.businessId";

	private static final String STATUSJSONKEY = "$.state.applicationStatus";

	private static final String AUDITDETAILSKEY = "auditDetails";

	private RestTemplate rest;

	private PropertyConfiguration config;

	@Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
	private Boolean pickWFServiceNameFromTradeTypeOnly;

	@Autowired
	public WorkflowIntegrator(RestTemplate rest, PropertyConfiguration config) {
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
	public void callWorkFlow(PropertyRequest request, String from) {

		String wfTenantId = request.getProperties().get(0).getTenantId();
		JSONArray array = new JSONArray();
		for (Property property : request.getProperties()) {
			String applicationNumber = "";
			JSONObject obj = new JSONObject();
			List<Map<String, String>> uuidmaps = new LinkedList<>();
			if (!CollectionUtils.isEmpty(property.getOwners())) {
				property.getOwners().forEach(owners -> {
					Map<String, String> uuidMap = new HashMap<>();
					uuidMap.put(UUIDKEY, owners.getId());
					uuidmaps.add(uuidMap);
				});
				applicationNumber = property.getOwners().get(0).getOwnerDetails().getApplicationNumber();
			}
			obj.put(TENANTIDKEY, wfTenantId);
			switch (from) {
			case "ME":
				obj.put(BUSINESSSERVICEKEY, config.getCSPBusinessServiceValue());
				obj.put(BUSINESSIDKEY, property.getTransitNumber());
				break;
			case "OT":
				obj.put(BUSINESSSERVICEKEY, config.getOwnershipTransferBusinessServiceValue());
				obj.put(BUSINESSIDKEY, applicationNumber);
				break;
			}

			obj.put(ACTIONKEY, property.getMasterDataAction());
			obj.put(MODULENAMEKEY, MODULENAMEVALUE);
			obj.put(AUDITDETAILSKEY, property.getAuditDetails());
			obj.put(COMMENTKEY, "");

			array.add(obj);
		}
		if (!array.isEmpty()) {
			JSONObject workFlowRequest = new JSONObject();
			workFlowRequest.put(REQUESTINFOKEY, request.getRequestInfo());
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
			Map<String, String> idStatusMap = new HashMap<>();
			responseArray.forEach(object -> {

				DocumentContext instanceContext = JsonPath.parse(object);
				idStatusMap.put(instanceContext.read(BUSINESSIDJOSNKEY), instanceContext.read(STATUSJSONKEY));
			});

			// setting the status back to Property object from wf response
			request.getProperties()
					.forEach(property -> property.setMasterDataState(idStatusMap.get(property.getTransitNumber())));
		}
	}
	
	public void callDuplicateCopyWorkFlow(DuplicateCopyRequest request) {

		String wfTenantId = request.getDuplicateCopyApplications().get(0).getTenantId();
		JSONArray array = new JSONArray();
		for (DuplicateCopy application : request.getDuplicateCopyApplications()) {
			JSONObject obj = new JSONObject();
			List<Map<String, String>> uuidmaps = new LinkedList<>();
			if (!CollectionUtils.isEmpty(application.getApplicant())) {
				application.getApplicant().forEach(owners -> {
					Map<String, String> uuidMap = new HashMap<>();
					uuidMap.put(UUIDKEY, owners.getId());
					uuidmaps.add(uuidMap);
				});
			}
			obj.put(TENANTIDKEY, wfTenantId);
			obj.put(BUSINESSSERVICEKEY, config.getDuplicateCopyBusinessServiceValue());
			obj.put(BUSINESSIDKEY, application.getId());
			obj.put(ACTIONKEY, application.getAction());
			obj.put(MODULENAMEKEY, MODULENAMEVALUE);
			obj.put(AUDITDETAILSKEY, application.getAuditDetails());
			obj.put(COMMENTKEY, "");

			array.add(obj);
		}
		if (!array.isEmpty()) {
			JSONObject workFlowRequest = new JSONObject();
			workFlowRequest.put(REQUESTINFOKEY, request.getRequestInfo());
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
			Map<String, String> idStatusMap = new HashMap<>();
			responseArray.forEach(object -> {

				DocumentContext instanceContext = JsonPath.parse(object);
				idStatusMap.put(instanceContext.read(BUSINESSIDJOSNKEY), instanceContext.read(STATUSJSONKEY));
			});

			// setting the status back to Property object from wf response
			request.getDuplicateCopyApplications()
					.forEach(application -> application.setState(idStatusMap.get(application.getId())));
		}
	}
}