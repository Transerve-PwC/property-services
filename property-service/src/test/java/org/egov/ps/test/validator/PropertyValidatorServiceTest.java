package org.egov.ps.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.EstateDocumentList;
import org.egov.ps.model.Owner;
import org.egov.ps.model.Role;
import org.egov.ps.service.MDMSService;
import org.egov.ps.service.WorkflowCreationService;
import org.egov.ps.validator.PropertyValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PropertyValidatorServiceTest {
	
	@InjectMocks
	private PropertyValidator propertyValidator;
	
	@Mock
	MDMSService mdmsservice;
	
	@Test
	public void testvalidateDocumentsOnTypePositive() throws Exception {
		
		//Step 1 - Request Info 
		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		
		//Step 2 - Main Document.json file master data
		String json = getFileContents("property_master_create_validate_document_owner.json");
		Owner owner = new Gson().fromJson(json, Owner.class);
		String documentJson = "[{\"code\":\"CERTIFIED_COPY_LEASE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"CERTIFIED_COPY_LEASE_DESCRIPTION\"},{\"code\":\"NOTARIZED_COPY_DEED\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NOTARIZED_COPY_DEED_DESCRIPTION\"},{\"code\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_DESCRIPTION\"},{\"code\":\"INDEMNITY_BOND_TRANSFEREE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"INDEMNITY_BOND_TRANSFEREE_DESCRIPTION\"},{\"code\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_WITNESSES_INDEMITY_BOND\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_WITNESSES_INDEMITY_BOND_DESCRIPTION\"},{\"code\":\"CLEARANCE_PREVIOUS_MORTGAGE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"CLEARANCE_PREVIOUS_MORTGAGE_DESCRIPTION\"},{\"code\":\"SEWERAGE_CONNECTION\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SEWERAGE_CONNECTION_DESCRIPTION\"},{\"code\":\"PROOF_OF_CONSTRUCTION\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"PROOF_OF_CONSTRUCTION_DESCRIPTION\"},{\"code\":\"INDEMNITY_BOND\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"INDEMNITY_BOND_DESCRIPTION\"},{\"code\":\"NOTARIZED_COPY_GPA_SPA\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NOTARIZED_COPY_GPA_SPA_DESCRIPTION\"},{\"code\":\"AFFIDAVIT_VALIDITY_GPA_SPA\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_VALIDITY_GPA_SPA_DESCRIPTION\"},{\"code\":\"AFFIDAVIT_EFFECT\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_EFFECT_DESCRIPTION\"},{\"code\":\"ATTESTED_COPY_PARTNERSHIP_DEED\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"ATTESTED_COPY_PARTNERSHIP_DEED_DESCRIPTION\"},{\"code\":\"COPY_OF_MEMORANDUM\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_EFFECT_DESCRIPTION\"},{\"code\":\"NO_DUE_CERTIFICATE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NO_DUE_CERTIFICATE_DESCRIPTION\"}]";
		
		Type listTypeDocument = new TypeToken<List<EstateDocumentList>>() {}.getType();
		List<EstateDocumentList> propertyList = new Gson().fromJson(documentJson, listTypeDocument);
		
		List<Map<String, Object>> fieldConfigurations = new ArrayList<Map<String,Object>>(0);
		for (EstateDocumentList estateDocumentListObj : propertyList) {
			Map<String, Object> tempMap = new HashMap<String, Object>(0);
			tempMap.put("code", estateDocumentListObj.getCode());
			tempMap.put("required", estateDocumentListObj.getRequired());
			tempMap.put("accept", estateDocumentListObj.getAccept());
			tempMap.put("fileType", estateDocumentListObj.getFileType());
			tempMap.put("description", estateDocumentListObj.getDescription());
			fieldConfigurations.add(tempMap);
		}
		
		//Step 3 - Mock mdmservice.
		Mockito.when(mdmsservice.getDocumentConfig("documents", requestInfo, "ch")).thenReturn(fieldConfigurations);
		
		//Step 4 - Test case
		Map<String, String> errorMap = new HashMap<String,String>();
		propertyValidator.validateDocumentsOnType(requestInfo, "ch", owner, errorMap, "");
		
	}
	
	@Test
	public void testvalidateDocumentsOnTypeNegative() throws Exception {
		
		//Step 1 - Request Info 
		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		
		//Step 2 - Main Document.json file master data
		String json = getFileContents("property_master_create_validate_document_owner.json");
		Owner owner = new Gson().fromJson(json, Owner.class);
		String documentJson = "[{\"code\":\"CERTIFIED_COPY_LEASE_\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"CERTIFIED_COPY_LEASE_DESCRIPTION\"},{\"code\":\"NOTARIZED_COPY_DEED\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NOTARIZED_COPY_DEED_DESCRIPTION\"},{\"code\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_DESCRIPTION\"},{\"code\":\"INDEMNITY_BOND_TRANSFEREE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"INDEMNITY_BOND_TRANSFEREE_DESCRIPTION\"},{\"code\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_WITNESSES_INDEMITY_BOND\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SELF_ATTESTED_PHOTO_IDENTITY_PROOF_WITNESSES_INDEMITY_BOND_DESCRIPTION\"},{\"code\":\"CLEARANCE_PREVIOUS_MORTGAGE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"CLEARANCE_PREVIOUS_MORTGAGE_DESCRIPTION\"},{\"code\":\"SEWERAGE_CONNECTION\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"SEWERAGE_CONNECTION_DESCRIPTION\"},{\"code\":\"PROOF_OF_CONSTRUCTION\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"PROOF_OF_CONSTRUCTION_DESCRIPTION\"},{\"code\":\"INDEMNITY_BOND\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"INDEMNITY_BOND_DESCRIPTION\"},{\"code\":\"NOTARIZED_COPY_GPA_SPA\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NOTARIZED_COPY_GPA_SPA_DESCRIPTION\"},{\"code\":\"AFFIDAVIT_VALIDITY_GPA_SPA\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_VALIDITY_GPA_SPA_DESCRIPTION\"},{\"code\":\"AFFIDAVIT_EFFECT\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_EFFECT_DESCRIPTION\"},{\"code\":\"ATTESTED_COPY_PARTNERSHIP_DEED\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"ATTESTED_COPY_PARTNERSHIP_DEED_DESCRIPTION\"},{\"code\":\"COPY_OF_MEMORANDUM\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"AFFIDAVIT_EFFECT_DESCRIPTION\"},{\"code\":\"NO_DUE_CERTIFICATE\",\"required\":false,\"accept\":\"application/msword,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,image/*\",\"fileType\":\"ALLTYPES\",\"description\":\"NO_DUE_CERTIFICATE_DESCRIPTION\"}]";
		
		Type listTypeDocument = new TypeToken<List<EstateDocumentList>>() {}.getType();
		List<EstateDocumentList> propertyList = new Gson().fromJson(documentJson, listTypeDocument);
		
		List<Map<String, Object>> fieldConfigurations = new ArrayList<Map<String,Object>>(0);
		for (EstateDocumentList estateDocumentListObj : propertyList) {
			Map<String, Object> tempMap = new HashMap<String, Object>(0);
			tempMap.put("code", estateDocumentListObj.getCode());
			tempMap.put("required", estateDocumentListObj.getRequired());
			tempMap.put("accept", estateDocumentListObj.getAccept());
			tempMap.put("fileType", estateDocumentListObj.getFileType());
			tempMap.put("description", estateDocumentListObj.getDescription());
			fieldConfigurations.add(tempMap);
		}
		
		//Step 3 - Mock mdmservice.
		Mockito.when(mdmsservice.getDocumentConfig("documents", requestInfo, "ch")).thenReturn(fieldConfigurations);
		
		//Step 4 - Test case
		Map<String, String> errorMap = new HashMap<String,String>();
		propertyValidator.validateDocumentsOnType(requestInfo, "ch", owner, errorMap, "");
		
	}
	
	public static String getFileContents(String fileName) {
		try {
			return IOUtils.toString(
					WorkflowCreationService.class.getClassLoader().getResourceAsStream(fileName), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testValidateUserRole() throws Exception {
		//Step 1 - Request Info 
		String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
		//String requestInfoJson = "{\"apiId\":\"Rainmaker\",\"ver\":\".01\",\"ts\":\"\",\"action\":\"_create\",\"did\":\"1\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"4a959094-5d43-4303-ba5c-3c19fdee0f48\",\"UserInfo\":{\"id\":1,\"uuid\":\"2743bf22-6499-4029-bd26-79e5d0ce6430\",\"userName\":\"ds\",\"name\":\"ds\",\"mobileNumber\":null,\"emailId\":null,\"locale\":null,\"type\":\"EMPLOYEE\",\"roles\":[{\"name\":\"TL BRANCH_ESTATE\",\"code\":\"ES_EB_APPROVER\",\"tenantId\":\"ch.chandigarh\"},{\"name\":null,\"code\":\"CPS_DS\",\"tenantId\":\"ch.chandigarh\"},{\"name\":\"Employee\",\"code\":\"EMPLOYEE\",\"tenantId\":\"ch.chandigarh\"}],\"active\":true,\"tenantId\":\"ch.chandigarh\"}}";
		RequestInfo requestInfo = new Gson().fromJson(requestInfoJson, RequestInfo.class);
		
		//Step 2 - Main Document.json file master data
		String json = getFileContents("property_master_create_validate_document_owner.json");
		Owner owner = new Gson().fromJson(json, Owner.class);
		
		String branchJson = "[{\"code\":\"BRANCH_ESTATE\",\"role\":\"ES_EB_APPROVER\"},{\"code\":\"BRANCH_BUILDING\",\"role\":\"ES_BB_APPROVER\"},{\"code\":\"BRANCH_MANIMAJRA\",\"role\":\"ES_MM_APPROVER\"}]";
		
		Type listType = new TypeToken<List<Role>>() {}.getType();
		List<Role>  list = new Gson().fromJson(branchJson, listType);
		
		List<Map<String, Object>> fieldConfigurations = new ArrayList<Map<String,Object>>(0);
		for (Role obj : list) {
			Map<String, Object> tempMap = new HashMap<String, Object>(0);
			tempMap.put("code", obj.getCode());
			tempMap.put("role", obj.getRole());
			fieldConfigurations.add(tempMap);
		}
		
		//Step 3 - Mock mdmservice.
		Mockito.when(mdmsservice.getBranchRoles("branchtype", requestInfo, "ch")).thenReturn(fieldConfigurations);
		
		//Step 4 - Test case
		Map<String, String> errorMap = new HashMap<String,String>();
		//propertyValidator.validateUserRole(requestInfo, errorMap);
	}

}
