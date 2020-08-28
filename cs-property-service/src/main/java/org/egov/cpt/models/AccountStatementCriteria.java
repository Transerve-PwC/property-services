package org.egov.cpt.models;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatementCriteria {

	@JsonProperty("fromDate")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fromDate;

	@JsonProperty("toDate")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date toDate;
	
	@JsonProperty("propertyid")
	private String propertyid;
	
	private List<String> paymentids;
	private List<String> demandids;
}
