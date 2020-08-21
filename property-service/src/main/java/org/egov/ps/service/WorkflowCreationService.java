package org.egov.ps.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.BusinessServiceRequest;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.BusinessService;
import org.egov.ps.web.contracts.BusinessServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

	Gson gson = new Gson();
	
	private static Map<String, List<ApplicationType>> templateMapping = new HashMap<String, List<ApplicationType>>(0);

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

	public List<WorkFlowResponseDetails> createWorkflows(RequestInfo requestInfo) throws Exception {
		List<WorkFlowResponseDetails> workFlowResponseDetailsLst = new ArrayList<WorkFlowResponseDetails>(0);
		String url = workflowContextPath+"/"+workflowBusinessServiceCreateApi;

		templateMapping.entrySet().stream().forEach(e -> {
			try {
				String workflowJson = getFileContents("workflows/"+e.getKey()+".json");
				e.getValue().stream().forEach(applicationType -> {
					//2. convert JSON String to work flow details ....
					BusinessService businessService = gson.fromJson(workflowJson, BusinessService.class);
					businessService.setBusinessService(applicationType.getName());

					if(null != businessService.getStates()) {
						businessService.getStates().stream().forEach(state -> {
							state.setState((null != state.getState() && !state.getState().isEmpty()) ? applicationType.getPrefix()+state.getState() :state.getState());
							if(null != state.getActions()) {
								state.getActions().stream().map(action -> {
									action.setCurrentState((null != action.getCurrentState() && !action.getCurrentState().isEmpty()) ? applicationType.getPrefix()+action.getCurrentState() :action.getCurrentState());
									action.setNextState((null != action.getNextState() && !action.getNextState().isEmpty()) ? applicationType.getPrefix()+action.getNextState() :action.getNextState());
									return action;
								}).collect(Collectors.toList());
							}
						});
					}

					List<BusinessService> lst = new ArrayList<BusinessService>(0);
					lst.add(businessService);

					//3. Build Request object for rest template ..start
					BusinessServiceRequest requestObj  = BusinessServiceRequest.builder()
							.businessServices(lst)
							.requestInfo(requestInfo)
							.build();

					//4. Rest Template  call
					//System.out.println(new Gson().toJson(requestObj));
					try {
						BusinessServiceResponse response = restTemplate.postForObject( url, requestObj, BusinessServiceResponse.class);
						workFlowResponseDetailsLst.add(WorkFlowResponseDetails.builder()
															.workFlowName(businessService.getBusinessService())
															.created(true)
															.message(response.getResponseInfo().getStatus().toString())
															.build());

					} catch (Exception e2) {
						e2.printStackTrace();
						workFlowResponseDetailsLst.add(WorkFlowResponseDetails.builder()
								.workFlowName(businessService.getBusinessService())
								.created(false)
								.message(e2.toString())
								.build());
					}
				});
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				workFlowResponseDetailsLst.add(WorkFlowResponseDetails.builder()
						.workFlowName(null)
						.created(false)
						.message(e1.getMessage().toString())
						.build());
			}
		});
		//System.out.println("size() ::"+workFlowResponseDetailsLst.size());
		return workFlowResponseDetailsLst;
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


@Builder
@Getter
@Setter
class WorkFlowResponseDetails {
	private String workFlowName;
	private boolean created;
	private String message;
} 
