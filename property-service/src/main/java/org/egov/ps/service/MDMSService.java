package org.egov.ps.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.config.Configuration;
import org.egov.ps.repository.ServiceRequestRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

@Service
public class MDMSService {

	@Autowired
	Configuration config;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private Util util;

	public List<Map<String, Object>> getApplicationConfig(String applicationType, RequestInfo requestInfo,
			String tenantId) throws JSONException {
//		TODO: change in mdms-data config
		tenantId = tenantId.split("\\.")[0];
		MdmsCriteriaReq mdmsCriteriaReq = util.prepareMdMsRequest(tenantId, PSConstants.MDMS_PS_MODULE_NAME,
				Arrays.asList(PSConstants.MDMS_PS_FIELDS), null, requestInfo);
		StringBuilder url = getMdmsSearchUrl(tenantId, applicationType);
		Object response = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);

		String MDMSResponsePath = "$.MdmsRes." + PSConstants.MDMS_PS_MODULE_NAME + "." + applicationType
				+ ".*.fields.*";

		List<Map<String, Object>> fieldConfigurations = JsonPath.read(response, MDMSResponsePath);
		return fieldConfigurations;
	}

	/**
	 * Creates and returns the url for mdms search endpoint
	 *
	 * @return MDMS Search URL
	 */
	private StringBuilder getMdmsSearchUrl(@NotNull final String tenantId, @NotNull final String masterName) {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint())
				.append("?tenantId=").append(tenantId).append("&moduleName=").append(PSConstants.MDMS_PS_MODULE_NAME)
				.append("&masterName=").append(masterName);
	}

}