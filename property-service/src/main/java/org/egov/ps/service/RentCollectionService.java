package org.egov.ps.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.ps.model.RentCollection;
import org.egov.ps.model.RentDemand;
import org.egov.ps.model.RentPayment;
import org.springframework.stereotype.Service;

@Service
public class RentCollectionService {

	public List<RentCollection> getCollectionsForPayment(ArrayList<RentDemand> demands, RentPayment payment) {
		return null;
	}
}
