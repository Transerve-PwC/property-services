package org.egov.ps.controller;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Matchers.anyInt;
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
import org.egov.ps.model.Auction;
import org.egov.ps.service.AuctionService;
import org.egov.ps.service.ReadExcelService;
import org.egov.ps.util.FileStoreUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReadExcelControllerTest {

	@InjectMocks
	ReadExcelController readExcelController;

	@Mock
	FileStoreUtils fileStoreUtils;

	@Mock
	ReadExcelService readExcelService;

	@Mock
	AuctionService auctionService;

	private MockMvc mockMvc;

	private static List<Auction> dummyAuctions = new ArrayList();
	private static String dummyFilePath = "";

	/* In Setup Functions */
	@Before
	public void setUp() {
		// Arrange
		setDummydata();
		mockMvc = MockMvcBuilders.standaloneSetup(readExcelController).build();

		when(fileStoreUtils.fetchFileStoreUrl(anyObject())).thenReturn(dummyFilePath);
		when(readExcelService.getDatafromExcel(anyObject(), anyInt())).thenReturn(dummyAuctions);

	}

	@Test
	public void testReadExcel() {
		try {
			
			String json = "{\"apiId\":\"Rainmaker\",\"ver\":\"01\",\"action\":\"_create\",\"key\":\"\",\"msgId\":\"20170310130900|en_IN\",\"authToken\":\"833b0a57-bbc5-4194-a961-bdb3794fa284\",\"userInfo\":{\"tenantId\":\"ch\",\"id\":8,\"username\":\"any\",\"mobile\":\"8866581197\",\"email\":\"mineshbhadeshia@gmail.com\" }}";
			RequestInfo requestInfo = new Gson().fromJson(json, RequestInfo.class);
			
			mockMvc.perform(MockMvcRequestBuilders
					.post("/v1/excel/read?tenantId=ch&fileStoreId=" + UUID.randomUUID().toString())
					.content(new Gson().toJson(requestInfo))
	                .contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)).andDo(print())
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("details").isArray());
		} catch (Exception e) {
			Assertions.assertThat(false);
		}

	}

	private void setDummydata() {
		dummyFilePath = "https://digit-s3.s3.ap-south-1.amazonaws.com/ch/RentedProperties/September/9/1599649503314Estate%20Branch%20Bidders%20excel%20.xlsx?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20200917T104745Z&X-Amz-SignedHeaders=host&X-Amz-Expires=86399&X-Amz-Credential=AKIA3CQUPMIX3BJDAIVF%2F20200917%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=51eb4959b6f0dd548dc9c74b5b0ecd88bcf8a87dc8a4224c028417029d9072be";
		for (int i = 0; i < 5; i++) {
			String count = String.valueOf(i + 1);
			dummyAuctions.add(Auction.builder().id(i).auctionDescription("Dummy AuctionDescription " + count)
					.depositDate(String.valueOf(new Date().getTime())).depositedEMDAmount((long) (i * 20))
					.emdValidityDate(String.valueOf(new Date().getTime()))
					.participatedBidders("Participated Bidders " + count).refundStatus("Refund Status " + count)
					.build());
		}
	}
}
