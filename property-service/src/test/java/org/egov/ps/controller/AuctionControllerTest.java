package org.egov.ps.controller;


import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.model.Property;
import org.egov.ps.service.AuctionService;
import org.egov.ps.util.ResponseInfoFactory;
import org.egov.ps.web.contracts.AuctionTransactionRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class AuctionControllerTest {

	private MockMvc mockMvc;
	
	@InjectMocks
	private AuctionController auctionController;
	
	@Mock
	private AuctionService auctionService;
	
	@Mock
	private ResponseInfoFactory responseInfoFactory;
	
	public static List<Auction> dummyAuctions = new ArrayList();
	public static RequestInfo dummyRequestInfo = new RequestInfo();
	public static ResponseInfo dummyResponseInfo = new ResponseInfo();
	
	/* In Setup Functions */
    @Before
    public void setUp() {
        // Arrange
    	setDummayData();
    	mockMvc = MockMvcBuilders.standaloneSetup(auctionController).build();
    	
    	// Mock results
    	when(auctionService.saveAuctionWithProperty(anyObject(), anyObject())).thenReturn(dummyAuctions);
    	when(responseInfoFactory.createResponseInfoFromRequestInfo(anyObject(),anyBoolean())).thenReturn(dummyResponseInfo);
    }
    
    @Test
    public void testCreate() {
    	   	
    	/*Prepare a Dummy object of AuctionTransactionRequest*/
    	AuctionTransactionRequest auctionTransactionRequest = new AuctionTransactionRequest();
		auctionTransactionRequest.setRequestInfo(dummyRequestInfo);
		
		/*Prepare a Dummy object of property*/
		Property dummyProperty = new Property();
		dummyProperty.setId("1");
				
		try {
			/* No property object had been set in auctionTransactionRequest */
			mockMvc.perform(MockMvcRequestBuilders
			        .post("/auctions/_create")
			        .content(new Gson().toJson(auctionTransactionRequest))
			        .contentType(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON))
			        .andDo(print())
			        .andExpect(status().isBadRequest());
			
			/* Property object had been set with only id and file store id and 
			 * tenant id are missing in auctionTransactionRequest */
			auctionTransactionRequest.setProperty(dummyProperty);
			mockMvc.perform(MockMvcRequestBuilders
			        .post("/auctions/_create")
			        .content(new Gson().toJson(auctionTransactionRequest))
			        .contentType(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON))
			        .andDo(print())
			        .andExpect(status().isBadRequest());
			
			dummyProperty.setFileNumber("file-Number123");
			dummyProperty.setTenantId("ch");
			dummyProperty.setCategory("Category 1");
			dummyProperty.setSectorNumber("Sector Number 1");
			dummyProperty.setSiteNumber("Site Number 1");
			dummyProperty.setState("state 1");
			dummyProperty.setSubCategory("subCategory");
			dummyProperty.setAction("action");
			
			auctionTransactionRequest.setProperty(dummyProperty);
			mockMvc.perform(MockMvcRequestBuilders
			        .post("/auctions/_create")
			        .param("tenantId", "ch")
			        .param("fileStoreId", "d4e4a4a2-030e-4509-974b-86c5003b4564")
			        .content(new Gson().toJson(auctionTransactionRequest))
			        .contentType(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON))
			        .andDo(print())
			        .andExpect(status().isCreated());
			
		} catch (Exception e) {
			 Assertions.assertThat(false);
		}
		
    }
    
    private void setDummayData() {
    	String json = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
    	dummyRequestInfo = new Gson().fromJson(json, RequestInfo.class);
    	dummyResponseInfo = ResponseInfo.builder().apiId("Rainmaker").ver("01").status("successful").build();
    	for(int i=0; i < 10; i++) {
    		dummyAuctions.add(Auction.builder()
	    		.id(UUID.randomUUID().toString())
	    		.auctionDescription("Auction Description "+i)
	    		.fileNumber("File-Number"+i)
	    		.depositDate(new Date().getTime())
	    		.participatedBidders("Participated Bidders "+i)
	    		.propertyId(UUID.randomUUID().toString())
	    		.build());
    	}
    }
}
