package org.egov.ps.model;

import org.hibernate.validator.constraints.NotBlank;

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
public class ExcelSearchCriteria {
	
	@NotBlank(message="URL parameter tenantId should not be empty or null")
	private String tenantId;

	@NotBlank(message="URL parameter fileStoreId should not be empty or null")
	private String fileStoreId;

}
