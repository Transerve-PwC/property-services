package org.egov.cpt.service;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.AuditDetails;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.PropertyCriteria;
import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.repository.PropertyRepository;
import org.egov.cpt.util.PTConstants;
import org.egov.cpt.util.PropertyUtil;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RentEnrichmentService {

	@Autowired
	PropertyUtil propertyutil;

	@Autowired
	PropertyRepository propertyRepository;

	public void enrichRentdata(PropertyRequest request) {
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {
				enrichPropertyRentdata(property, request.getRequestInfo());
			});
		}
	}

	private void enrichPropertyRentdata(Property property, RequestInfo requestInfo) {

		if (CollectionUtils.isEmpty(property.getDemands())) {
			return;
		}

		final PropertyCriteria criteria = PropertyCriteria.builder().propertyId(property.getId()).build();
		RentAccount account = propertyRepository.getPropertyRentAccountDetails(criteria);
		if (account == null) {
			account = RentAccount.builder().remainingAmount(0D).id(UUID.randomUUID().toString())
					.propertyId(property.getId()).tenantId(property.getTenantId())
					.auditDetails(propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true)).build();
		}
		property.setRentAccount(account);

		/**
		 * Delete existing data as new data is coming in.
		 */
		if (property.getDemands().stream().filter(demand -> demand.getId() == null || demand.getId().isEmpty())
				.findAny().isPresent()) {
			List<RentDemand> demands = propertyRepository.getPropertyRentDemandDetails(criteria);
			 List<RentPayment> payments = propertyRepository.getPropertyRentPaymentDetails(criteria);
			property.setInActiveDemands(demands);
			property.setInActivePayments(payments);
			account.setRemainingAmount(0D);
		}

		property.getDemands().forEach(demand -> {
			if (demand.getId() == null) {
				AuditDetails demandAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(),
						true);
				demand.setId(UUID.randomUUID().toString());
				demand.setPropertyId(property.getId());
				demand.setRemainingPrincipal(demand.getCollectionPrincipal());
				demand.setInterestSince(demand.getGenerationDate());
				demand.setMode(RentDemand.ModeEnum.fromValue(PTConstants.MODE_UPLOADED));
				demand.setTenantId(property.getTenantId());
				demand.setAuditDetails(demandAuditDetails);
			}
		});

		property.getPayments().forEach(payment -> {
			if (payment.getId() == null) {
				AuditDetails paymentAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(),
						true);
				payment.setId(UUID.randomUUID().toString());
				payment.setPropertyId(property.getId());
				payment.setMode(RentPayment.ModeEnum.fromValue(PTConstants.MODE_UPLOADED));
				payment.setTenantId(property.getTenantId());
				payment.setAuditDetails(paymentAuditDetails);
			}
		});
	}

	public void enrichCollection(PropertyRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		if (!CollectionUtils.isEmpty(request.getProperties())) {
			request.getProperties().forEach(property -> {

				if (!CollectionUtils.isEmpty(property.getRentCollections())) {
					property.getRentCollections().forEach(collection -> {
						if (collection.getId() == null) {
							AuditDetails rentAuditDetails = propertyutil
									.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
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
