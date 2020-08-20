package org.egov.ps.service;

@Builder
@Getter
@Setter
class ApplicationType {
    private String prefix;
    private String name;
}

public class WorkflowCreationService {

    private Map<String, List<ApplicationType>> templateMapping;

    public WorkflowCreationService() {
        templateMapping.put("template-ownership-transfer", Arrays.toList(
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build(),
            ApplicationType.builder().name("EstateProperty-OwnershipTransfer-SaleDeed").prefix("ES_EB_SD_").build()
        ));
    }

    public void createWorkflows(RequestInfo requestInfo) {

        //for each entry in map

        //Get the template from entry.key.
        BusinessService templateBusinessService;

        //for each of the value in entry.value

        //Modify the templateBusinessService by prefix all the states and modify the `businessService`

        // Call WorkflowService /egov-workflow-v2/egov-wf/businessservice/_create

        

    }
}