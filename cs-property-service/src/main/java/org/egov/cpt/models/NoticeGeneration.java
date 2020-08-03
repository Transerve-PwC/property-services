package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.cpt.util.PropertySerializer;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Property
 */
@ApiModel(description = "A Object holds the basic data for a Notice Generation")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NoticeGeneration {

	@JsonProperty("id")
	private String id;

	@JsonSerialize(using = PropertySerializer.class)
	private Property property;

	@JsonProperty("tenantId")
	private String tenantId;
	

	@JsonProperty("memoNumber")
	private String memoNumber;
	
	@JsonProperty("memoDate")
	private Long memoDate;
	
	@JsonProperty("noticeType")
	private String noticeType;
	
	@JsonProperty("guardian")
	private String guardian;
	
	@JsonProperty("relationship")
	private String relationship;
	
	@JsonProperty("violations")
	private String violations;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("demandNoticeFrom")
	private Long demandNoticeFrom;
	
	@JsonProperty("demandNoticeTo")
	private Long demandNoticeTo;
	
	@JsonProperty("recoveryType")
	private String recoveryType;
	
	@JsonProperty("amount")
	private Double amount;
	
	@Valid
	@JsonProperty("applicationDocuments")
	private List<Documents> applicationDocuments = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
	public NoticeGeneration addApplicationDocumentsItem(Documents applicationDocumentsItem) {
		if (this.applicationDocuments == null) {
			this.applicationDocuments = new ArrayList<>();
		}
		for (Documents applicationDocument : applicationDocuments) {
			if (applicationDocument.getId().equalsIgnoreCase(applicationDocumentsItem.getId())) {
				return this;
			}
		}
		this.applicationDocuments.add(applicationDocumentsItem);
		return this;
	}
	


}
