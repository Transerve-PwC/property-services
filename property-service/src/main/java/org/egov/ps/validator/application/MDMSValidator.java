package org.egov.ps.validator.application;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.config.Configuration;
import org.egov.ps.repository.ServiceRequestRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IApplicationValidator;
import org.egov.ps.validator.IValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ApplicationValidator("mdms")
@Component
public class MDMSValidator implements IApplicationValidator {
	
	@Autowired
	Configuration config;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private Util util;

    @Override
    public List<String> validate(IValidation validation, IApplicationField field, Object value, Object parent) {
        Map<String, Object> params = validation.getParams();
        String moduleName = (String) params.get("moduleName");
        String masterName = (String) params.get("masterName");
        
        String tenantId = PSConstants.TENENT_ID;
        RequestInfo requestInfo = (RequestInfo) params.get("requestInfo");
        // We need to make a MDMS call.
        // "code"
        // if (value is one of the masters.code)
        
        tenantId = tenantId.split("\\.")[0];
		MdmsCriteriaReq mdmsCriteriaReq = util.prepareMdMsRequest(tenantId, moduleName,
				Arrays.asList(PSConstants.MDMS_PS_PATH_FILTER), null, null);
		StringBuilder url = getMdmsSearchUrl(tenantId, moduleName, masterName);
		Object response = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
        
        return null;
    }
    
	private StringBuilder getMdmsSearchUrl(@NotNull final String tenantId, @NotNull final String moduleName, @NotNull final String masterName) {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint())
				.append("?tenantId=").append(tenantId).append("&moduleName=").append(moduleName).append("&masterName=")
				.append(masterName);
	}

}