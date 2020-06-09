package org.egov.cpt.service;

import org.egov.cpt.web.contracts.PropertyRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	public void createUser(PropertyRequest request) {

//		Property property = request.getProperty();
//		RequestInfo requestInfo = request.getRequestInfo();
//		Role role = getCitizenRole();
//		Set<String> listOfMobileNumbers = getMobileNumbers(property, requestInfo, property.getTenantId());
//
//		property.getOwners().forEach(owner -> {
//
//			addUserDefaultFields(property.getTenantId(), role, owner);
//			UserDetailResponse userDetailResponse = userExists(owner, requestInfo);
//
//			if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
//
//				/*
//				 * Sets userName equal to mobileNumber
//				 * 
//				 * If mobileNumber already assigned as user-name for another user
//				 * 
//				 * then random uuid is assigned as user-name
//				 */
//				StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userCreateEndpoint);
//				setUserName(owner, listOfMobileNumbers);
//
//				CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(owner)
//						.build();
//
//				userDetailResponse = userCall(userRequest, uri);
//
//				if (ObjectUtils.isEmpty(userDetailResponse)) {
//					throw new CustomException("INVALID USER RESPONSE",
//							"The user create has failed for the mobileNumber : " + owner.getUserName());
//				}
//			} else {
//				owner.setId(userDetailResponse.getUser().get(0).getId());
//				owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
//				addUserDefaultFields(property.getTenantId(), role, owner);
//
//				StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userUpdateEndpoint);
//				userDetailResponse = userCall(new CreateUserRequest(requestInfo, owner), uri);
//				if (userDetailResponse.getUser().get(0).getUuid() == null) {
//					throw new CustomException("INVALID USER RESPONSE", "The user updated has uuid as null");
//				}
//			}
//			// Assigns value of fields from user got from userDetailResponse to owner object
//			setOwnerFields(owner, userDetailResponse, requestInfo);
//		});
	}

//	private Role getCitizenRole() {
//		return Role.builder().code("CITIZEN").name("Citizen").build();
//	}

}
