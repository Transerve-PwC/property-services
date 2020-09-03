package org.egov.cpt.web.contracts;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.NoticeGeneration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeGenerationRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("NoticeApplications")
	@Valid
	private List<NoticeGeneration> noticeApplications;
}
