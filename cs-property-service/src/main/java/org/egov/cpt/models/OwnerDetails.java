package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnerDetails {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("ownerId")
	private String ownerId;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("dateOfBirth")
	private Long dateOfBirth;

	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@JsonProperty("allotmentStartdate")
	private Long allotmentStartdate;

	@JsonProperty("allotmentEnddate")
	private Long allotmentEnddate;

	@JsonProperty("posessionStartdate")
	private Long posessionStartdate;

	@JsonProperty("posessionEnddate")
	private Long posessionEnddate;

	@JsonProperty("monthlyRent")
	private String monthlyRent;

	@JsonProperty("revisionPeriod")
	private String revisionPeriod;

	@JsonProperty("revisionPercentage")
	private String revisionPercentage;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@Valid
	@JsonProperty("payment")
	private List<Payment> payment;

	@JsonProperty("fatherOrHusband")
	private String fatherOrHusband;

	@JsonProperty("relation")
	private String relation;

	public enum ApplicationTypeEnum {
		NEW("MasterEntry"),

		RENEW("CitizenApplication");

		private String value;

		ApplicationTypeEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static ApplicationTypeEnum fromValue(String text) {
			for (ApplicationTypeEnum b : ApplicationTypeEnum.values()) {
				if (String.valueOf(b.value).equalsIgnoreCase(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@JsonProperty("applicationType")
	private ApplicationTypeEnum applicationType = null;

	@JsonProperty("relationWithDeceasedAllottee")
	private String relationWithDeceasedAllottee;

	@JsonProperty("dateOfDeathAllottee")
	private Long dateOfDeathAllottee;

	@JsonProperty("applicationNumber")
	private String applicationNumber;

	@JsonProperty("permanent")
	private Boolean permanent;

	@Valid
	@JsonProperty("ownershipTransferDocuments")
	private List<OwnershipTransferDocument> ownershipTransferDocuments;

	public OwnerDetails addownershipTransferDocumentsItem(OwnershipTransferDocument ownershipTransferDocumentsItem) {
		if (this.ownershipTransferDocuments == null) {
			this.ownershipTransferDocuments = new ArrayList<>();
		}
		if (!this.ownershipTransferDocuments.contains(ownershipTransferDocumentsItem))
			this.ownershipTransferDocuments.add(ownershipTransferDocumentsItem);
		return this;
	}

}
