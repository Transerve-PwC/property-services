package org.egov.cpt.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hpsf.Array;
import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RentEnrichmentService {
	@Autowired
	PropertyUtil propertyutil;
	
	public void enrichRentdata(PropertyRequest request){
		RequestInfo requestInfo = request.getRequestInfo();
		List<RentDemand> inActiveDemand = new ArrayList();
		List<RentPayment> inActivePayment = new ArrayList();
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				
				if (!CollectionUtils.isEmpty(property.getDemands())) {
					property.getDemands().forEach(demand -> {
						if (demand.getId() == null) {
							AuditDetails demandAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
							demand.setId(UUID.randomUUID().toString());
							demand.setPropertyId(property.getId());
							demand.setRemainingPrincipal(demand.getCollectionPrincipal());
							demand.setInterestSince(demand.getGenerationDate());
							demand.setMode(RentDemand.ModeEnum.fromValue(PTConstants.MODE_UPLOADED));
							demand.setTenantId(property.getTenantId());
							demand.setAuditDetails(demandAuditDetails);
						}
						else{
							inActiveDemand.add(demand);
//							property.getDemands().remove(demand);
						}
						
					});
				}	
				property.setInActiveDemands(inActiveDemand);
				if(!CollectionUtils.isEmpty(inActiveDemand))
					property.getDemands().remove(inActiveDemand);
				if (!CollectionUtils.isEmpty(property.getPayments())) {
					property.getPayments().forEach(payment -> {
						if (payment.getId() == null) {
							AuditDetails paymentAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
							payment.setId(UUID.randomUUID().toString());
							payment.setPropertyId(property.getId());
							payment.setMode(RentPayment.ModeEnum.fromValue(PTConstants.MODE_UPLOADED));
							payment.setTenantId(property.getTenantId());
							payment.setAuditDetails(paymentAuditDetails);
						}else{
							inActivePayment.add(payment);
//							property.getPayments().remove(payment);
						}
						

					});
				}
				property.setInActivePayments(inActivePayment);
				if(!CollectionUtils.isEmpty(inActivePayment))
					property.getPayments().remove(inActivePayment);
				
				if (property.getRentAccount()!=null) {
					if (property.getRentAccount().getId() == null) {
						AuditDetails rentAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
						property.getRentAccount().setId(UUID.randomUUID().toString());
						property.getRentAccount().setPropertyId(property.getId());
						property.getRentAccount().setTenantId(property.getTenantId());
						property.getRentAccount().setAuditDetails(rentAuditDetails);
					}
				}
			});
		}
		
	}

	public void enrichCollection(PropertyRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				
				if (!CollectionUtils.isEmpty(property.getRentCollections())) {
					property.getRentCollections().forEach(collection -> {
						if (collection.getId() == null) {
							AuditDetails rentAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
							collection.setId(UUID.randomUUID().toString());
							collection.setTenantId(property.getTenantId());
							collection.setAuditDetails(rentAuditDetails);
						}

					});
				}
			});
		}
		
	}
}
