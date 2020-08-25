package org.egov.cpt.service;

import java.util.Collections;

import com.jayway.jsonpath.JsonPath;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.util.PropertyUtil;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MDMSService {
    
    @Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
	private ServiceRequestRepository serviceRequestRepository;

    public Object getMDMSResponse(RequestInfo requestInfo, String tenantId, String moduleName, String masterName, String filter, String responsePath) {
        StringBuilder uri = new StringBuilder(mdmsHost).append(mdmsEndpoint);

		MdmsCriteriaReq criteriaReq = propertyUtil.prepareMdMsRequest(tenantId, moduleName, Collections.singletonList(masterName), filter, requestInfo);

		try {
			Object result = serviceRequestRepository.fetchResult(uri, criteriaReq);
			return JsonPath.read(result, responsePath);
		} catch (Exception e) {
			log.error("Error while fetching MDMS data", e);
			throw new CustomException("NO MASTER DATA", "No data found for this mdms criteria");
		}
    }
}