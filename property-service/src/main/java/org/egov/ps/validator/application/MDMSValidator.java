package org.egov.ps.validator.application;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.ps.annotation.ApplicationValidator;
import org.egov.ps.config.Configuration;
import org.egov.ps.repository.ServiceRequestRepository;
import org.egov.ps.service.MDMSService;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IApplicationValidator;
import org.egov.ps.validator.IValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

@ApplicationValidator("mdms")
@Component
public class MDMSValidator implements IApplicationValidator {

	@Autowired
	Configuration config;

	@Autowired
	MDMSService mdmsService;
	
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
		RequestInfo requestInfo = RequestInfo.builder().authToken("authToken").build();

		tenantId = tenantId.split("\\.")[0];
		MdmsCriteriaReq mdmsCriteriaReq = util.prepareMdMsRequest(tenantId, moduleName, Arrays.asList(masterName),
				PSConstants.MDMS_PS_PATH_FILTER, requestInfo);
		StringBuilder url = getMdmsSearchUrl(tenantId, moduleName, masterName);
		Object response = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
		
//		List<Map<String, Object>> fieldConfigurations = mdmsService.getApplicationConfig(moduleName, requestInfo, tenantId);

		String MDMSResponsePath = "$.MdmsRes." + moduleName + "." + masterName;

		List<String> allowedValues = JsonPath.read(response, MDMSResponsePath);

		if (!allowedValues.stream().filter(allowedValue -> allowedValue.equals(value)).findAny().isPresent()) {
			return Arrays.asList(String.format("Value '%s' not found in expected mdms values [%s] for path '%s'", value,
					StringUtils.join(allowedValues, ","), field.getPath()));
		}
		return null;
	}

	private StringBuilder getMdmsSearchUrl(@NotNull final String tenantId, @NotNull final String moduleName,
			@NotNull final String masterName) {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsSearchEndpoint())
				.append("?tenantId=").append(tenantId).append("&moduleName=").append(moduleName).append("&masterName=")
				.append(masterName);
	}

}