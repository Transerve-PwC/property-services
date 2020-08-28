package org.egov.cpt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RentSummary {

	@Builder.Default
	double balancePrincipal = 0D;

	@Builder.Default
	double balanceInterest = 0D;

	@Builder.Default
	double balanceAmount = 0D;
}
