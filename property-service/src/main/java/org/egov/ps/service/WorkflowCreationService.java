package org.egov.ps.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Application;
import org.egov.ps.model.BusinessServiceRequest;
import org.egov.ps.model.WorkFlowDetails;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.BusinessService;
import org.egov.ps.web.contracts.BusinessServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.parser.JSONParser;

@Service
@PropertySource(value = "classpath:config.properties", ignoreResourceNotFound = true)
public class WorkflowCreationService {

	@Autowired
	Util util;

	@Autowired
	RestTemplate restTemplate;

	@Value("${workflow.workDir.path}")
	private String workflowWorkDirPath;

	@Value("${workflow.context.path}")
	private String workflowContextPath;

	@Value("${workflow.businessservice.create.path}")
	private String workflowBusinessServiceCreateApi;

	private Map<String, List<ApplicationType>> templateMapping;

	public WorkflowCreationService() {
		templateMapping.put("template-ownership-transfer", Arrays.asList(
				//ES_EB_SD_ = Estate Service Estate Branch Sale Deed
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-RegisteredWill").prefix("ES_EB_RW_").build(),
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-UnRegisteredWill").prefix("ES_EB_URW_").build(),
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-InstestateDeath").prefix("ES_EB_ID_").build(),
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-PatnershipDeed").prefix("ES_EB_PD_").build(),
				ApplicationType.builder().name("EstateProperty-OwnershipTransfer-FamilySettlement").prefix("ES_EB_FS_").build(),

				ApplicationType.builder().name("EstateProperty-OtherCitizenService-NOC").prefix("ES_EB_NOC_").build(),
				ApplicationType.builder().name("EstateProperty-OtherCitizenService-NDC").prefix("ES_EB_NDC_").build(),
				ApplicationType.builder().name("EstateProperty-OtherCitizenService-Mortgage").prefix("ES_EB_MORTGAGE_").build(),
				ApplicationType.builder().name("EstateProperty-OtherCitizenService-DuplicateCopy").prefix("ES_EB_DC_").build(),
				ApplicationType.builder().name("EstateProperty-OtherCitizenService-ResidentailToCommercial").prefix("ES_EB_RTC_").build(),
				ApplicationType.builder().name("EstateProperty-OtherCitizenService-ChangeInTrade").prefix("ES_EB_CIT_").build()
				));
	}

	public void createWorkflows(RequestInfo requestInfo) throws Exception {

		//1. Read workflow json
		String workflowJson = getFileContents("/workflows/template-ownership-transfer.json");

		//2. convert JSON String to work flow details ....
		JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		WorkFlowDetails workflowDetails = (WorkFlowDetails) parser.parse(workflowJson);

		//3. Build Request object for rest template ..start
		BusinessService businessService = BusinessService.builder()
				.tenantId(workflowDetails.getTenantId())
				.uuid("")
				.businessService(workflowDetails.getBusinessService())
				.business(workflowDetails.business)
				.getUri(null)
				.postUri(null)
				.businessServiceSla(workflowDetails.getBusinessServiceSla())
				.states(workflowDetails.getStates())
				.auditDetails(util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true))
				.build();

		List<BusinessService> lst = new ArrayList<BusinessService>(0);
		lst.add(businessService);
		BusinessServiceRequest requestObj  = BusinessServiceRequest.builder()
				.businessServices(lst)
				.requestInfo(requestInfo)
				.build();

		//Rest Template  call
		String url = workflowContextPath+"/"+workflowBusinessServiceCreateApi;
		BusinessServiceResponse response = restTemplate.postForObject( url, requestObj, BusinessServiceResponse.class);


		//for each entry in map

		//Get the template from entry.key.
		//BusinessService templateBusinessService;

		//for each of the value in entry.value

		//Modify the templateBusinessService by prefix all the states and modify the `businessService`

		// Call WorkflowService /egov-workflow-v2/egov-wf/businessservice/_create

	}

	public static String getFileContents(String fileName) {
		try {
			return IOUtils.toString(
					WorkflowCreationService.class.getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

@Builder
@Getter
@Setter
class ApplicationType {
	private String prefix;
	private String name;
}
