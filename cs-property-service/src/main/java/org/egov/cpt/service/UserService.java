package org.egov.cpt.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.cpt.models.UserDetailResponse;
import org.egov.cpt.models.UserSearchRequest;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

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

	/**
	 * provides a user search request with basic mandatory parameters
	 * 
	 * @param tenantId
	 * @param requestInfo
	 * @return
	 */
	public UserSearchRequest getBaseUserSearchRequest(String transitNumber, RequestInfo requestInfo) {
		return UserSearchRequest.builder().requestInfo(requestInfo).transitNumber(transitNumber).build();
	}

	/**
	 * Returns user using user search based on propertyCriteria(owner
	 * name,mobileNumber,userName)
	 * 
	 * @param criteria
	 * @param requestInfo
	 * @return serDetailResponse containing the user if present and the responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {
		StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
		UserDetailResponse userDetailResponse = userCall(userSearchRequest, uri);
		return userDetailResponse;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and object
	 * 
	 * @param userRequest Request object for user service
	 * @param uri         The address of the endpoint
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("unchecked")
	private UserDetailResponse userCall(Object userRequest, StringBuilder uri) {

		String dobFormat = null;
		if (uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint))
			dobFormat = "yyyy-MM-dd";
		else if (uri.toString().contains(userCreateEndpoint))
			dobFormat = "dd/MM/yyyy";
		try {
			Optional<Object> response = (Optional<Object>) serviceRequestRepository.fetchResult(uri, userRequest);

			if (response.isPresent()) {
				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.get();
				parseResponse(responseMap, dobFormat);
				UserDetailResponse userDetailResponse = mapper.convertValue(responseMap, UserDetailResponse.class);
				return userDetailResponse;
			} else {
				return new UserDetailResponse();
			}
		}
		// Which Exception to throw?
		catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * 
	 * @param responeMap LinkedHashMap got from user api response
	 * @param dobFormat  dob format (required because dob is returned in different
	 *                   format's in search and create response in user service)
	 */
	@SuppressWarnings("unchecked")
	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate", dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", dateTolong((String) map.get("pwdExpiryDate"), format1));
			});
		}
	}

	/**
	 * Converts date to long
	 * 
	 * @param date   date to be parsed
	 * @param format Format of the date
	 * @return Long value of date
	 */
	private Long dateTolong(String date, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = f.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

//	private Role getCitizenRole() {
//		return Role.builder().code("CITIZEN").name("Citizen").build();
//	}

}
