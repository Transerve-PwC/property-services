package org.egov.ps.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.config.Configuration;
import org.egov.ps.model.Application;
import org.egov.ps.model.Owner;
import org.egov.ps.model.OwnerDetails;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.producer.Producer;
import org.egov.ps.repository.PropertyRepository;
import org.egov.ps.util.PSConstants;
import org.egov.ps.util.Util;
import org.egov.ps.web.contracts.ApplicationRequest;
import org.egov.ps.web.contracts.AuditDetails;
import org.egov.ps.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostApprovalEnrichmentService {

	@Autowired
	Util util;

	@Autowired
	private Configuration config;

	@Autowired
	PropertyRepository propertyRepository;

	@Autowired
	private Producer producer;

	public void ownershipTransferPostEnrichment(ApplicationRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails newOwnerAuditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		if (!CollectionUtils.isEmpty(request.getApplications())) {
			request.getApplications().forEach(application -> {

				if (application.getProperty().getId() != null) {
					PropertyCriteria propertySearchCriteria = PropertyCriteria.builder()
							.propertyId(application.getProperty().getId()).build();

					List<Property> properties = propertyRepository.getProperties(propertySearchCriteria);

					if (!CollectionUtils.isEmpty(properties)) {
						properties.forEach(property -> {

							if (!CollectionUtils.isEmpty(property.getPropertyDetails().getOwners())) {
								property.getPropertyDetails().getOwners().forEach(currentOwner -> {

									OwnerDetails currentOwnerDetails = null;
									double actualOwnerShare = 0;
									double salePercentage = 0;

									String masterName = application.getBranchType() + "_" + application.getModuleType()
											+ "_" + application.getApplicationType();
									switch (masterName) {
									case PSConstants.EB_OT_SD:

										JsonNode purchaser = null;
										if (application.getApplicationDetails().get("owner") != null) {
											JsonNode ownerWhoIsSelling = application.getApplicationDetails()
													.get("owner");
											String ownerIdWhoIsSelling = ownerWhoIsSelling.get("id").asText();

											if (ownerIdWhoIsSelling.contentEquals(currentOwner.getId())) {
												currentOwnerDetails = currentOwner.getOwnerDetails();
												purchaser = application.getApplicationDetails().get("purchaser");

												actualOwnerShare = currentOwner.getShare();
												salePercentage = purchaser.get("percentageOfShareTransferred")
														.asDouble();

												currentOwner.setShare(actualOwnerShare - salePercentage);
												if (actualOwnerShare == salePercentage) {
													currentOwnerDetails.setIsCurrentOwner(false);
												}
											}
										}

										/**
										 * If purchaser is an existing owner else purchaser will be new owner
										 */
										if ((purchaser.get("id") != null
												|| !purchaser.get("id").asText().contentEquals(""))
												&& purchaser.get("id").asText().contentEquals(currentOwner.getId())) {
											currentOwner.setShare(actualOwnerShare + salePercentage);

											currentOwnerDetails.setIsCurrentOwner(true);
											currentOwnerDetails.setAllotmentNumber(null);
											currentOwnerDetails.setDateOfAllotment(System.currentTimeMillis());
										} else {
											Owner newOwnerItem = getOwnerFromPurcheser(application, property,
													newOwnerAuditDetails);
											property.getPropertyDetails().addOwnerItem(newOwnerItem);
										}

										break;

									case PSConstants.EB_OT_RW:

										JsonNode transferee = null;
										if (application.getApplicationDetails().get("deceased") != null) {
											JsonNode deceasedOwner = application.getApplicationDetails()
													.get("deceased");
											String deceasedOwnerId = deceasedOwner.get("id").asText();

											if (deceasedOwnerId.contentEquals(currentOwner.getId())) {
												currentOwnerDetails = currentOwner.getOwnerDetails();
												transferee = application.getApplicationDetails().get("transferee");

												actualOwnerShare = currentOwner.getShare();
												salePercentage = transferee.get("percentageOfShareTransferred")
														.asDouble();

												currentOwner.setShare(actualOwnerShare - salePercentage);
												if (actualOwnerShare == salePercentage) {
													currentOwnerDetails.setIsCurrentOwner(false);
												}
											}
										}

										/**
										 * If purchaser is an existing owner else purchaser will be new owner
										 */
										if ((transferee.get("id") != null
												|| !transferee.get("id").asText().contentEquals(""))
												&& transferee.get("id").asText().contentEquals(currentOwner.getId())) {
											currentOwner.setShare(actualOwnerShare + salePercentage);

											currentOwnerDetails.setIsCurrentOwner(true);
											currentOwnerDetails.setAllotmentNumber(null);
											currentOwnerDetails.setDateOfAllotment(System.currentTimeMillis());
										} else {
											Owner newOwnerItem = getOwnerFromTransferee(application, property,
													newOwnerAuditDetails);
											property.getPropertyDetails().addOwnerItem(newOwnerItem);
										}

										break;

									default:
										break;
									}

								});
							}

						});
					}

					/**
					 * Update the property by sending to the persistor.
					 */
					PropertyRequest propertyRequest = new PropertyRequest();
					propertyRequest.setRequestInfo(requestInfo);
					propertyRequest.setProperties(properties);
					producer.push(config.getUpdatePropertyTopic(), propertyRequest);
				}
			});
		}
	}

	private Owner getOwnerFromPurcheser(Application application, Property property, AuditDetails newOwnerAuditDetails) {

		JsonNode purchaser = application.getApplicationDetails().get("purchaser");
		String gen_new_owner_id = UUID.randomUUID().toString();
		String gen_new_owner_details_id = UUID.randomUUID().toString();

		OwnerDetails newOwnerDetails = OwnerDetails.builder().id(gen_new_owner_details_id)
				.tenantId(application.getTenantId()).ownerId(gen_new_owner_id).ownerName(purchaser.get("name").asText())
				.guardianName(purchaser.get("fatherOrHusbandName").asText())
				.guardianRelation(purchaser.get("relation").asText()).mobileNumber(purchaser.get("mobileNo").asText())

				.possesionDate(null).allotmentNumber(null) // TODO allotment number mandatory field
				.dateOfAllotment(System.currentTimeMillis()) // TODO what if purchaser is existing owner

				.isCurrentOwner(true).isMasterEntry(false).dueAmount(BigDecimal.ZERO)
				.address(purchaser.get("address").asText()).auditDetails(newOwnerAuditDetails).build();

		Owner newOwner = Owner.builder().id(gen_new_owner_id).tenantId(application.getTenantId())
				.propertyDetailsId(property.getPropertyDetails().getId())
				.share(purchaser.get("percentageOfShareTransferred").asDouble())

				.cpNumber(null).serialNumber(null) // TODO serial number mandatory field

				.ownerDetails(newOwnerDetails).auditDetails(newOwnerAuditDetails).build();

		return newOwner;
	}

	private Owner getOwnerFromTransferee(Application application, Property property,
			AuditDetails newOwnerAuditDetails) {

		JsonNode transferee = application.getApplicationDetails().get("transferee");
		String gen_new_owner_id = UUID.randomUUID().toString();
		String gen_new_owner_details_id = UUID.randomUUID().toString();

		OwnerDetails newOwnerDetails = OwnerDetails.builder().id(gen_new_owner_details_id)
				.tenantId(application.getTenantId()).ownerId(gen_new_owner_id)
				.ownerName(transferee.get("name").asText()).guardianName(transferee.get("fatherOrHusbandName").asText())
				.guardianRelation(transferee.get("relation").asText()).mobileNumber(transferee.get("mobileNo").asText())

				.possesionDate(null).allotmentNumber(null) // TODO allotment number mandatory field
				.dateOfAllotment(System.currentTimeMillis()) // TODO what if purchaser is existing owner

				.isCurrentOwner(true).isMasterEntry(false).dueAmount(BigDecimal.ZERO)
				.address(transferee.get("address").asText()).auditDetails(newOwnerAuditDetails).build();

		Owner newOwner = Owner.builder().id(gen_new_owner_id).tenantId(application.getTenantId())
				.propertyDetailsId(property.getPropertyDetails().getId())
				.share(transferee.get("percentageOfShareTransferred").asDouble())

				.cpNumber(null).serialNumber(null) // TODO serial number mandatory field

				.ownerDetails(newOwnerDetails).auditDetails(newOwnerAuditDetails).build();

		return newOwner;
	}

}
