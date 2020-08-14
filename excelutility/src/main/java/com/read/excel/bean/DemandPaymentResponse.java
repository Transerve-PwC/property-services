package com.read.excel.bean;

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
public class DemandPaymentResponse {

	List<Demand> Demand= new ArrayList<>();
	List<Payment> Payment= new ArrayList<>();
}
