package org.egov.cpt.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RentDemandResponse {

	List<RentDemand> demand= new ArrayList<>();
	List<RentPayment> payment= new ArrayList<>();
}
