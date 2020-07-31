package org.egov.cpt.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.cpt.models.Mortgage;
import org.egov.cpt.models.NoticeGeneration;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeGenerationResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("noticeApplications")
	@Valid
	private List<NoticeGeneration> noticeApplications;

	public NoticeGenerationResponse addPropertiesItem(NoticeGeneration applicationItem) {
		if (this.noticeApplications == null) {
			this.noticeApplications = new ArrayList<>();
		}
		this.noticeApplications.add(applicationItem);
		return this;
	}

}
