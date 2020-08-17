package org.egov.ps.service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.jayway.jsonpath.JsonPath;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.config.Configuration;
import org.egov.ps.repository.ServiceRequestRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MDMSService {

    private final String MODULE_NAME = "EstateProperties";

    private final String MDMSResponsePath = "$.MdmsRes.BillingService.BusinessService";
    @Autowired
    Configuration config;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    public List<Map<String, Object>> getApplicationConfig(String applicationType, RequestInfo requestInfo,
            String tenantId) throws JSONException {
        MdmsCriteriaReq mdmsCriteriaReq = null;
        StringBuilder url = getMdmsSearchUrl(tenantId, applicationType);
        String result = (String) serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
        List<Map<String, Object>> fieldConfigurations = JsonPath.read(result, MDMSResponsePath);
        // JSONObject fieldConfigurations = new JSONObject(result);
        return fieldConfigurations;
    }

    /**
     * Creates and returns the url for mdms search endpoint
     *
     * @return MDMS Search URL
     */
    private StringBuilder getMdmsSearchUrl(@NotNull final String tenantId, @NotNull final String masterName) {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsGetEndpoint()).append("?tenantId=")
                .append(tenantId).append("&moduleName=").append(MODULE_NAME).append("&masterName=").append(masterName);
    }

}